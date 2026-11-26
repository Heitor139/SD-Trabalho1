from pathlib import Path
import re
import socket
import subprocess
import tempfile
import time

BUILD = Path(__file__).resolve().parents[1] / "servidor/build-trabalho4"
JAVA = ["java", "-cp", str(BUILD)]
HOST = "127.0.0.1"


def portas_livres():
    with socket.socket() as primeira, socket.socket() as segunda:
        for porta in (primeira, segunda):
            porta.bind((HOST, 0))
        return str(primeira.getsockname()[1]), str(segunda.getsockname()[1])


def aguardar(condicao):
    prazo = time.monotonic() + 35
    while time.monotonic() < prazo:
        valor = condicao()
        if valor:
            return valor
        time.sleep(0.1)
    raise AssertionError("Tempo esgotado aguardando o processo.")


def verificar(raiz):
    porta_votos, porta_broker = portas_livres()
    processos = []

    def iniciar(nome, classe, *args, cliente=False):
        log = raiz / (nome + ".log")
        with log.open("ab") as saida:
            processo = subprocess.Popen(
                JAVA + ["votacao.console." + classe, *args], cwd=raiz,
                stdin=subprocess.PIPE if cliente else subprocess.DEVNULL,
                stdout=saida, stderr=subprocess.STDOUT, text=True,
            )
        processos.append(processo)
        return processo, log

    def broker(nome):
        processo, log = iniciar(nome, "ServidorIntermediario", HOST, porta_broker, str(raiz / "estado"))
        aguardar(lambda: b"pronto na porta" in log.read_bytes())
        return processo

    def cliente(nome):
        return iniciar(nome, "ConsoleNotificacoes", HOST, porta_votos, porta_broker, cliente=True)

    def digitar(processo, comando):
        assert processo.poll() is None
        processo.stdin.write(comando + "\n")
        processo.stdin.flush()

    def obter_id(processo, log, comando):
        posicao = log.stat().st_size
        digitar(processo, comando)
        nome = comando.split()[0]
        padrao = rb"\[" + nome.encode("ascii") + rb"\] ([a-f0-9-]{36})"
        encontrado = aguardar(lambda: re.search(padrao, log.read_bytes()[posicao:]))
        return encontrado.group(1).decode("ascii")

    def publicar(processo, log, rotulo, texto, tipo="VOTACAO"):
        posicao = log.stat().st_size
        digitar(processo, f"publicar {tipo} {rotulo} ALERTA {texto}")
        recibo = f"[publicar] Publicado em {tipo}/{rotulo}".encode("ascii")
        aguardar(lambda: recibo in log.read_bytes()[posicao:])

    def sair(processo):
        digitar(processo, "sair")
        assert processo.wait(timeout=10) == 0

    try:
        intermediario = broker("broker")
        _, log_servidor = iniciar("servidor", "ServidorVotacao", HOST, porta_votos, HOST, porta_broker)
        aguardar(lambda: b"pronto na porta" in log_servidor.read_bytes())
        ana, log_ana = cliente("ana")
        bia, log_bia = cliente("bia")
        caio, log_caio = cliente("caio")
        editor, log_editor = cliente("editor")
        id_ana = obter_id(ana, log_ana, "voto-confirmado ana turma")
        assert obter_id(ana, log_ana, "voto-confirmado ana turma") == id_ana
        id_bia = obter_id(bia, log_bia, "voto-confirmado bia diretoria")
        id_caio = obter_id(caio, log_caio, "voto-confirmado caio turma")
        id_grupo = obter_id(bia, log_bia, "assinar bia turma")
        assert len({id_ana, id_bia, id_caio, id_grupo}) == 4

        digitar(ana, "acompanhar " + id_ana)
        digitar(bia, "acompanhar " + id_bia)
        digitar(caio, "acompanhar " + id_caio)
        publicar(editor, log_editor, "turma", "aviso da turma")
        publicar(editor, log_editor, "diretoria", "aviso da diretoria")
        publicar(editor, log_editor, "turma", "aviso do grupo", tipo="NOTIFICACAO")
        aguardar(lambda: b"aviso da turma" in log_ana.read_bytes())
        aguardar(lambda: b"aviso da turma" in log_caio.read_bytes())
        aguardar(lambda: b"aviso da diretoria" in log_bia.read_bytes())
        digitar(bia, "consultar " + id_grupo)
        aguardar(lambda: b"[NOTIFICACAO/turma] [ALERTA] aviso do grupo" in log_bia.read_bytes())
        assert b"aviso da turma" not in log_bia.read_bytes()
        assert b"aviso da diretoria" not in log_ana.read_bytes()
        assert b"aviso do grupo" not in log_ana.read_bytes()

        sair(ana)
        publicar(editor, log_editor, "turma", "aviso para assinante desligado")
        intermediario.terminate()
        intermediario.wait(timeout=10)
        posicao = log_editor.stat().st_size
        digitar(editor, "publicar VOTACAO turma ALERTA tentativa sem intermediario")
        aguardar(lambda: b"Connection refused" in log_editor.read_bytes()[posicao:]
                 or b"Falha de" in log_editor.read_bytes()[posicao:])
        broker("broker-reiniciado")

        ana, log_ana = cliente("ana-retorno")
        digitar(ana, "consultar " + id_ana)
        aguardar(lambda: b"aviso para assinante desligado" in log_ana.read_bytes())
        digitar(ana, "consultar " + id_ana)
        aguardar(lambda: b"[consultar] Sem avisos." in log_ana.read_bytes())
        digitar(ana, "cancelar " + id_ana)
        aguardar(lambda: b"[cancelar]" in log_ana.read_bytes())
        digitar(ana, "consultar " + id_ana)
        aguardar(lambda: b"[consultar] Erro:" in log_ana.read_bytes())
        print("OK: RMI, sessoes persistentes, dois topicos de votacao e grupo com o mesmo nome.")
        print("OK: fan-out, assinante offline, reinicio do intermediario, consumo e cancelamento.")
    except Exception:
        for log in raiz.glob("*.log"):
            print(log.name, log.read_bytes().decode(errors="replace"))
        raise
    finally:
        for processo in reversed(processos):
            if processo.poll() is None:
                processo.terminate()
            processo.wait(timeout=10)


if __name__ == "__main__":
    with tempfile.TemporaryDirectory(prefix="votacao-rmi-", dir=BUILD) as diretorio:
        verificar(Path(diretorio))

