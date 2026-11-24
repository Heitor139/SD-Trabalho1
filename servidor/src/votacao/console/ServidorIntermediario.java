package votacao.console;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public final class ServidorIntermediario {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Uso: ServidorIntermediario <diretório de mensagens>");
        }
        Path raiz = Files.createDirectories(Path.of(args[0]));
        var caixas = new CaixaMensagens(raiz);
        var estado = new EstadoNotificacoes(raiz);
        try (var canal = FileChannel.open(raiz.resolve("estado.lock"), StandardOpenOption.CREATE,
                StandardOpenOption.WRITE); var bloqueio = canal.tryLock()) {
            if (bloqueio == null) {
                throw new IOException("Já existe um intermediário neste diretório.");
            }
            System.out.println("Intermediário pronto.");
            String ultimoId = estado.ultimoId();
            if (ultimoId != null) {
                Path ultimaMensagem = raiz.resolve("intermediario").resolve(ultimoId);
                if (Files.exists(ultimaMensagem)) {
                    processar(caixas, estado, ultimaMensagem);
                }
            }
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
