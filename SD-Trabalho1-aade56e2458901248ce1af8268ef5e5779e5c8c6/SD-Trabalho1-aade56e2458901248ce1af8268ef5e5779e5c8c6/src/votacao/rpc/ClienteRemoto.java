package votacao.rpc;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import votacao.modelo.Mensagem;

public final class ClienteRemoto extends UnicastRemoteObject implements RpcEndpoint {
    private static final long serialVersionUID = 1L;

    public ClienteRemoto() throws RemoteException { super(0); }

    @Override
    public byte[] exchange(byte[] requestBytes) throws RemoteException {
        try {
            RpcMessage request = (RpcMessage) RpcCodec.decode(requestBytes);
            Serializable argument = (Serializable) RpcCodec.decode(request.getArguments());
            if (request.getMethodId() == MetodosRpc.RECEBER_MENSAGEM) {
                Mensagem mensagem = (Mensagem) argument;
                System.out.println("[" + mensagem.getTipo() + "] " + mensagem.getMensagem());
            }
            return RpcCodec.encode(new RpcMessage(RpcMessage.REPLY, request.getRequestId(),
                    request.getObjectReference(), request.getMethodId(), RpcCodec.encode("OK")));
        } catch (Exception error) {
            throw new RemoteException("Falha ao receber notificacao", error);
        }
    }
}
