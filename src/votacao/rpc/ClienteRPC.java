package votacao.rpc;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

public final class ClienteRPC {
    private ClienteRPC() { }

    public static void main(String[] args) throws Exception {
        String servidorHost = args.length > 0 ? args[0] : "localhost";
        int servidorPorta = args.length > 1 ? Integer.parseInt(args[1]) : 1099;
        String clienteHost = args.length > 2 ? args[2] : "localhost";
        int clientePorta = args.length > 3 ? Integer.parseInt(args[3]) : 1100;
        String id = args.length > 4 ? args[4] : UUID.randomUUID().toString();
        System.setProperty("java.rmi.server.hostname", clienteHost);
        Registry registry = LocateRegistry.createRegistry(clientePorta);
        ClienteRemoto callback = new ClienteRemoto();
        registry.rebind("ClienteRPC", callback);
        RemoteObjectRef servidor = new RemoteObjectRef(servidorHost, servidorPorta, "VotacaoRPC");
        RemoteObjectRef referenciaCliente = new RemoteObjectRef(clienteHost, clientePorta,
                "ClienteRPC");
        RpcClient.doOperation(servidor, MetodosRpc.ENTRAR_GRUPO,
                new Object[] { id, referenciaCliente });
        System.out.println("Conectado como " + id + ". Comandos: listar, sair.");
        try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String comando = scanner.nextLine().trim();
                if (comando.equalsIgnoreCase("sair")) break;
                if (comando.equalsIgnoreCase("listar")) {
                    Object clientes = RpcClient.doOperation(servidor,
                            MetodosRpc.LISTAR_CLIENTES, new Object[0]);
                    System.out.println(clientes);
                }
            }
        } finally {
            try {
                RpcClient.doOperation(servidor, MetodosRpc.SAIR_GRUPO,
                        new Object[] { id });
            } finally {
                UnicastSupport.unexport(callback);
                UnicastSupport.unexport(registry);
            }
        }
    }
}
