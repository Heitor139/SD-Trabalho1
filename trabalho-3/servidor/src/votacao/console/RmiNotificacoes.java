package votacao.console;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

final class RmiNotificacoes {
    private static final String NOME = "Notificacoes";
    private static Registry registroPublicado;
    private static RedeNotificacoes servicoPublicado;

    static void publicar(String host, int porta, RedeNotificacoes servico) throws RemoteException {
        System.setProperty("java.rmi.server.hostname", host);
        registroPublicado = LocateRegistry.createRegistry(porta);
        servicoPublicado = servico;
        registroPublicado.rebind(NOME, UnicastRemoteObject.exportObject(servicoPublicado, porta));
    }

    static RedeNotificacoes conectar(String host, int porta) throws RemoteException {
        try {
            return (RedeNotificacoes) LocateRegistry.getRegistry(host, porta).lookup(NOME);
        } catch (NotBoundException erro) {
            throw new RemoteException("Serviço de notificações indisponível.", erro);
        }
    }
}
