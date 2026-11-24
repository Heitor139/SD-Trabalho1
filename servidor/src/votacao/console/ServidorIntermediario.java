package votacao.console;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.rmi.RemoteException;

public final class ServidorIntermediario implements RedeNotificacoes {
    private final CaixaMensagens caixas;

    private ServidorIntermediario(CaixaMensagens caixas) {
        this.caixas = caixas;
    }

    @Override
    public String enviar(String[] comando) throws RemoteException {
        if (comando == null) {
            throw new IllegalArgumentException("Informe um comando.");
        }
        ComandosNotificacoes.interpretar(comando);
        try {
            return caixas.enviar("intermediario", comando);
        } catch (IOException erro) {
            throw new RemoteException("Não foi possível enfileirar o comando.", erro);
        }
    }

    @Override
    public String resultado(String protocolo) throws RemoteException {
        try {
            return caixas.resultado(protocolo);
        } catch (IOException erro) {
            throw new RemoteException("Não foi possível ler o recibo.", erro);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Uso: ServidorIntermediario <host anunciado> <porta> <diretório de estado>");
        }
        Path raiz = Files.createDirectories(Path.of(args[2]));
        var caixas = new CaixaMensagens(raiz);
        var estado = new EstadoNotificacoes(raiz);
        try (var canal = FileChannel.open(raiz.resolve("estado.lock"), StandardOpenOption.CREATE,
                StandardOpenOption.WRITE); var bloqueio = canal.tryLock()) {
            if (bloqueio == null) {
                throw new IOException("Já existe um intermediário neste diretório.");
            }
            String ultimoId = estado.ultimoId();
            if (ultimoId != null) {
                Path ultimaMensagem = raiz.resolve("intermediario").resolve(ultimoId);
                if (Files.exists(ultimaMensagem)) {
                    processar(caixas, estado, ultimaMensagem);
                }
            }
            RmiNotificacoes.publicar(args[0], Integer.parseInt(args[1]), new ServidorIntermediario(caixas));
            System.out.println("Intermediário pronto na porta " + args[1] + ".");
            Path falhaAnterior = null;
            while (true) {
                var pendentes = caixas.pendentes("intermediario");
                if (falhaAnterior != null) {
                    pendentes = new ArrayList<>(pendentes);
                    pendentes.remove(falhaAnterior);
                    pendentes.add(0, falhaAnterior);
                }
                for (Path mensagem : pendentes) {
                    try {
                        processar(caixas, estado, mensagem);
                        falhaAnterior = null;
                    } catch (IOException erro) {
                        falhaAnterior = mensagem;
                        System.err.println("A mensagem será tentada novamente: " + erro.getMessage());
                        break;
                    }
                }
                Thread.sleep(250);
            }
        } catch (IOException | RuntimeException erro) {
            System.err.println(erro.getMessage());
            System.exit(1);
        }
    }

    private static void processar(CaixaMensagens caixas, EstadoNotificacoes estado, Path mensagem) throws IOException {
        String id = mensagem.getFileName().toString();
        if (caixas.resultado(id) != null) {
            Files.delete(mensagem);
            return;
        }
        long inicio = System.nanoTime();
        String[] comando = Files.readAllLines(mensagem).toArray(String[]::new);
        String resultado = estado.executar(id, comando);
        caixas.registrar(id, resultado);
        Files.delete(mensagem);
        if (comando.length > 0 && comando[0].equals("publicar")) {
            System.out.printf("Publicação processada em %.2f ms%n", (System.nanoTime() - inicio) / 1_000_000.0);
        }
    }
}
