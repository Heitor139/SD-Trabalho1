package votacao.rpc;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicInteger;

public final class RpcClient {
    private static final AtomicInteger REQUEST_IDS = new AtomicInteger();

    private RpcClient() { }

    public static Serializable doOperation(RemoteObjectRef reference, int methodId,
            Serializable arguments) throws Exception {
        int requestId = REQUEST_IDS.incrementAndGet();
        byte[] encodedArguments = RpcCodec.encode(arguments);
        RpcMessage request = new RpcMessage(RpcMessage.REQUEST, requestId, reference,
                methodId, encodedArguments);
        Registry registry = LocateRegistry.getRegistry(reference.getHost(), reference.getPort());
        RpcEndpoint endpoint = (RpcEndpoint) registry.lookup(reference.getObjectName());
        RpcMessage reply = (RpcMessage) RpcCodec.decode(endpoint.exchange(RpcCodec.encode(request)));
        if (reply.getMessageType() != RpcMessage.REPLY || reply.getRequestId() != requestId) {
            throw new IllegalStateException("Resposta RPC nao corresponde a requisicao");
        }
        if (reply.getMethodId() == -1) {
            throw new IllegalStateException((String) RpcCodec.decode(reply.getArguments()));
        }
        return (Serializable) RpcCodec.decode(reply.getArguments());
    }
}
