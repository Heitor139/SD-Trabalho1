package votacao.rpc;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import votacao.modelo.Mensagem;

public final class ServidorRPC {
    private ServidorRPC() { }

    public static void main(String[] args) throws Exception {

        int porta = args.length > 0 ? Integer.parseInt(args[0]) : 1099;
        String host = args.length > 1 ? args[1] : "localhost";

        System.setProperty("java.rmi.server.hostname", host);

        Registry registry = LocateRegistry.createRegistry(porta);

        RpcServerEndpoint endpoint = new RpcServerEndpoint(new ServidorNotificacoes());

        registry.rebind("VotacaoRPC", endpoint);

        System.out.println("Servidor RPC ativo em " + host + ":" + porta);
        System.out.println("Digite notificacao, alerta, atualizacao ou sair.");

        try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {

            while (scanner.hasNextLine()) {

                String tipo = scanner.nextLine().trim();

                if (tipo.equalsIgnoreCase("sair")) break;
                if (tipo.isEmpty()) continue;
                if (!scanner.hasNextLine()) break;

                String texto = scanner.nextLine();

                Mensagem mensagem = tipo.equalsIgnoreCase("alerta")
                        ? new votacao.modelo.Alerta(texto)
                        : tipo.equalsIgnoreCase("atualizacao")
                                ? new Mensagem(Mensagem.ATUALIZACAO, texto)
                                : new votacao.modelo.Notificacao(texto);

                Object resultado = RpcClient.doOperation(
                        new RemoteObjectRef(host, porta, "VotacaoRPC"),
                        MetodosRpc.ENVIAR_NOTIFICACAO, new Object[] { mensagem });

                System.out.println(resultado);
            }
        }
        UnicastSupport.unexport(endpoint);
        UnicastSupport.unexport(registry);
    }
}
