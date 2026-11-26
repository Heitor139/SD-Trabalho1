package votacao.rpc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import votacao.modelo.Mensagem;

public final class ServidorNotificacoes implements RpcHandler {
    private final GrupoClientes grupo = new GrupoClientes();

    @Override
    public Serializable invoke(int methodId, Serializable arguments) throws Exception {
        Object[] values = (Object[]) arguments;

        if (methodId == MetodosRpc.ENTRAR_GRUPO) {

            grupo.entrar((String) values[0], (RemoteObjectRef) values[1]);
            return "Cliente registrado";

        }
        if (methodId == MetodosRpc.SAIR_GRUPO) {

            grupo.sair((String) values[0]);
            return "Cliente removido";

        }
        if (methodId == MetodosRpc.LISTAR_CLIENTES) {

            return new ArrayList<>(grupo.snapshot().keySet());

        }
        if (methodId == MetodosRpc.ENVIAR_NOTIFICACAO) {

            Mensagem mensagem = (Mensagem) values[0];
            int entregues = 0;

            for (Map.Entry<String, RemoteObjectRef> membro : grupo.snapshot().entrySet()) {
                try {

                    RpcClient.doOperation(membro.getValue(), MetodosRpc.RECEBER_MENSAGEM,
                            mensagem);
                    entregues++;

                } catch (Exception error) {

                    grupo.sair(membro.getKey());
                    
                }
            }
            return "Mensagem entregue a " + entregues + " cliente(s)";
        }
        throw new IllegalArgumentException("Metodo RPC desconhecido: " + methodId);
    }
}
