package votacao.rpc;

import java.io.Serializable;

public interface RpcHandler {
    Serializable invoke(int methodId, Serializable arguments) throws Exception;
}
