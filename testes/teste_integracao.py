from pathlib import Path
import subprocess
import re
import tempfile
import time
import uuid

BUILD = Path(__file__).resolve().parents[1] / "servidor/build-trabalho4"
JAVA = ["java", "-cp", str(BUILD)]


def comando(raiz, *args):
    return subprocess.check_output(
        JAVA + ["votacao.console.ConsoleNotificacoes", str(raiz), *args],
        text=True, timeout=30,
    ).strip()


def parar(processo):
    if processo.poll() is None:
        processo.terminate()
    processo.wait(timeout=10)


def aguardar(condicao):
    prazo = time.monotonic() + 30
    while not condicao():
        assert time.monotonic() < prazo, "Tempo esgotado aguardando o arquivo."
        time.sleep(0.1)


def recibo(raiz, identificador):
    arquivo = raiz / "recibos" / identificador
    aguardar(arquivo.is_file)
    return arquivo.read_text(encoding="utf-8")


def postar(raiz, *args):
    pasta = raiz / "intermediario"
    pasta.mkdir(exist_ok=True)
    identificador = f"{time.time_ns() // 1_000_000}-{uuid.uuid4()}"
    temporario = pasta / (identificador + ".tmp")
    temporario.write_text("\n".join(args) + "\n", encoding="utf-8")
    temporario.rename(pasta / identificador)
    return identificador


def verificar(raiz):
    processos = []

    def iniciar(classe):
        processo = subprocess.Popen(JAVA + ["votacao.console." + classe, str(raiz)],
                                    stdout=subprocess.DEVNULL)
        processos.append(processo)
        return processo

    def executar(*args):
        return recibo(raiz, postar(raiz, *args))

    def iniciar_cliente(nome):
        log = raiz / (nome + ".log")
        with log.open("wb") as saida:
            processo = subprocess.Popen(
                JAVA + ["votacao.console.ConsoleNotificacoes", str(raiz)],
                stdin=subprocess.PIPE, stdout=saida, text=True,
            )
        processos.append(processo)
        return processo, log

    def digitar(cliente, comando):
        cliente.stdin.write(comando + "\n")
        cliente.stdin.flush()

    def verificar_sessoes():
        assinante, log_assinante = iniciar_cliente("assinante")
        publicador, log_publicador = iniciar_cliente("publicador")
        digitar(assinante, "voto-confirmado carla teste-sessao")
        padrao = rb"\[voto-confirmado\] ([a-f0-9-]{36})"
        aguardar(lambda: re.search(padrao, log_assinante.read_bytes()))
        assinatura = re.search(padrao, log_assinante.read_bytes()).group(1).decode("ascii")
        digitar(assinante, "acompanhar " + assinatura)
        aguardar(lambda: b"Acompanhando " in log_assinante.read_bytes())
        for texto in ("primeiro aviso com espacos", "segundo aviso na mesma sessao"):
            digitar(publicador, "publicar VOTACAO teste-sessao ALERTA " + texto)
            aguardar(lambda: texto.encode("ascii") in log_assinante.read_bytes())
        aguardar(lambda: log_publicador.read_bytes().count(b"[publicar]") == 2)
        for cliente in (assinante, publicador):
            assert cliente.poll() is None
            digitar(cliente, "sair")
            assert cliente.wait(timeout=10) == 0

    try:
        voto = comando(raiz, "voto-confirmado", "ana", "turma")
        assert (raiz / "votos" / voto).is_file()
        assert comando(raiz, "resultado", voto) == "Pendente."
        iniciar("ServidorVotacao")
        broker = iniciar("ServidorIntermediario")
        ana = recibo(raiz, voto)
        bia = executar("voto-confirmado", "bia", "turma")
        grupo = executar("assinar", "ana", "turma")
        assert len({ana, bia, grupo}) == 3
        assert executar("voto-confirmado", "ana", "turma") == ana

        executar("publicar", "VOTACAO", "turma", "ALERTA", "aviso inicial")
        assert "aviso inicial" in executar("consultar", ana)
        parar(broker)
        aviso = comando(raiz, "publicar", "VOTACAO", "turma", "ALERTA", "aviso sem processos")
        assert (raiz / "intermediario" / aviso).is_file()
        assert not (raiz / "recibos" / aviso).exists()
        iniciar("ServidorIntermediario")
        assert "VOTACAO/turma" in recibo(raiz, aviso)

        executar("publicar", "NOTIFICACAO", "turma", "NOTIFICACAO", "mesmo nome")
        executar("publicar", "VOTACAO", "outra", "ALERTA", "outro destino")
        consulta = comando(raiz, "consultar", ana)
        lote_ana = recibo(raiz, consulta)
        assert comando(raiz, "resultado", consulta) == lote_ana
        lote_bia = executar("consultar", bia)
        lote_grupo = executar("consultar", grupo)
        assert "aviso sem processos" in lote_ana and len(lote_ana.splitlines()) == 1, lote_ana
        assert lote_bia.index("aviso inicial") < lote_bia.index("aviso sem processos"), lote_bia
        assert len(lote_bia.splitlines()) == 2, lote_bia
        assert "mesmo nome" in lote_grupo and len(lote_grupo.splitlines()) == 1, lote_grupo
        assert executar("consultar", ana) == ""
        executar("cancelar", grupo)
        assert executar("consultar", grupo).startswith("Erro:")
        assert executar("publicar", "INVALIDO", "turma", "ALERTA", "erro").startswith("Erro:")
        verificar_sessoes()
        print("OK: duas sessoes abertas, dois avisos e encerramento por sair.")
        print("OK: publicacao com assinante e intermediario desligados, recuperacao e roteamento por rotulo.")
        print("OK: servidor de votacao, dois assinantes, ordem, consumo, cancelamento e entrada invalida.")
    finally:
        for processo in reversed(processos):
            parar(processo)


if __name__ == "__main__":
    with tempfile.TemporaryDirectory(prefix="votacao-t4-", dir=BUILD) as diretorio:
        verificar(Path(diretorio))

