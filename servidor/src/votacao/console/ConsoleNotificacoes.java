package votacao.console;

import java.io.IOException;
import java.util.Arrays;

public final class ConsoleNotificacoes {
    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            if (args.length < 3) {
                throw new IllegalArgumentException(ComandosNotificacoes.USO);
            }
            var rede = new ClienteRede(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            if (args.length == 3) {
                new SessaoCliente(rede).executar();
                return;
            }
            String[] comando = Arrays.copyOfRange(args, 3, args.length);
            switch (comando[0]) {
                case "resultado" -> {
                    validarId(comando);
                    String recebido = rede.resultado(comando[1]);
                    System.out.println(recebido == null ? "Pendente." : recebido);
                }
                case "acompanhar" -> {
                    validarId(comando);
                    acompanhar(rede, comando[1]);
                }
                default -> System.out.println(rede.enviar(comando));
            }
        } catch (IllegalArgumentException erro) {
            System.err.println(erro.getMessage());
            System.exit(1);
        }
    }

    static void validarId(String[] comando) {
        if (comando.length != 2) {
            throw new IllegalArgumentException(ComandosNotificacoes.USO);
        }
    }

    private static void acompanhar(ClienteRede rede, String assinatura) throws IOException, InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            String id = rede.enviar(new String[]{"consultar", assinatura});
            String recebido;
            while ((recebido = rede.resultado(id)) == null) {
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
