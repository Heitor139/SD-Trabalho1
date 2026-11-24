package votacao.rpc;

import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

public final class UnicastSupport {

    private UnicastSupport() { }
    
    public static void unexport(Remote remote) throws Exception {
        UnicastRemoteObject.unexportObject(remote, true);
    }
}
