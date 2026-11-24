package votacao.console;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public final class ConsoleNotificacoes {
    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            if (args.length == 0) {
                throw new IllegalArgumentException(ComandosNotificacoes.USO);
            }
            var caixas = new CaixaMensagens(Path.of(args[0]));
            if (args.length == 1) {
                new SessaoCliente(caixas).executar();
                return;
            }
            String[] comando = Arrays.copyOfRange(args, 1, args.length);
            switch (comando[0]) {
                case "resultado" -> {
                    validarId(comando);
                    String recebido = caixas.resultado(comando[1]);
                    System.out.println(recebido == null ? "Pendente." : recebido);
                }
                case "acompanhar" -> {
                    validarId(comando);
                    acompanhar(caixas, comando[1]);
                }
                default -> System.out.println(enviar(caixas, comando));
            }
        } catch (IllegalArgumentException erro) {
            System.err.println(erro.getMessage());
            System.exit(1);
        }
    }

    static String enviar(CaixaMensagens caixas, String[] comando) throws IOException {
        ComandosNotificacoes.interpretar(comando);
        String destino = comando[0].equals("voto-confirmado") ? "votos" : "intermediario";
        return caixas.enviar(destino, comando);
    }

    static void validarId(String[] comando) {
        if (comando.length != 2) {
            throw new IllegalArgumentException(ComandosNotificacoes.USO);
        }
    }

    private static void acompanhar(CaixaMensagens caixas, String assinatura) throws IOException, InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            String id = caixas.enviar("intermediario", new String[]{"consultar", assinatura});
            String recebido;
            while ((recebido = caixas.resultado(id)) == null) {
                Thread.sleep(250);
            }
            if (!recebido.isEmpty()) {
                System.out.println(recebido);
            }
            if (recebido.startsWith("Erro: ")) {
                return;
            }
            Thread.sleep(2000);
        }
    }
}
