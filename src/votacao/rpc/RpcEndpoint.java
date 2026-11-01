package votacao.rpc;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RpcEndpoint extends Remote {
    byte[] exchange(byte[] request) throws RemoteException;
}
