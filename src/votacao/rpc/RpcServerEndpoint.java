package votacao.rpc;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public final class RpcServerEndpoint extends UnicastRemoteObject implements RpcEndpoint {
    private static final long serialVersionUID = 1L;
    private final RpcHandler handler;

    public RpcServerEndpoint(RpcHandler handler) throws RemoteException {
        super(0);
        this.handler = handler;
    }

    @Override
    public byte[] exchange(byte[] requestBytes) throws RemoteException {
        RpcMessage request = null;
        try {
            request = (RpcMessage) RpcCodec.decode(requestBytes);
            if (request.getMessageType() != RpcMessage.REQUEST) {
                throw new IllegalArgumentException("Tipo de mensagem invalido");
            }
            Serializable arguments = (Serializable) RpcCodec.decode(request.getArguments());
            Serializable result = handler.invoke(request.getMethodId(), arguments);
            return RpcCodec.encode(new RpcMessage(RpcMessage.REPLY, request.getRequestId(),
                    request.getObjectReference(), request.getMethodId(), RpcCodec.encode(result)));
        } catch (Exception error) {
            try {
                String message = error.getMessage() == null ? error.getClass().getSimpleName()
                        : error.getMessage();
                return RpcCodec.encode(new RpcMessage(RpcMessage.REPLY,
                        request == null ? -1 : request.getRequestId(), null, -1,
                        RpcCodec.encode(message)));
            } catch (Exception encodingError) {
                throw new RemoteException("Falha ao codificar resposta RPC", encodingError);
            }
        }
    }
}
