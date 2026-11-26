package votacao.rpc;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public final class GrupoClientes implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, RemoteObjectRef> membros = new LinkedHashMap<>();

    public synchronized void entrar(String id, RemoteObjectRef referencia) {
        membros.put(id, referencia);
    }

    public synchronized void sair(String id) { membros.remove(id); }

    public synchronized Map<String, RemoteObjectRef> snapshot() {
        return new LinkedHashMap<>(membros);
    }
}
