package votacao.console;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RedeNotificacoes extends Remote {
    String enviar(String[] comando) throws RemoteException;
    String resultado(String protocolo) throws RemoteException;
}
