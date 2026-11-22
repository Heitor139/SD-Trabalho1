package votacao.modelo;

import java.util.ArrayList;
import java.util.List;

public final class Assinatura {
    private final String id;
    private final String clienteId;
    private final Topico topico;
    private final List<Evento> eventosPendentes = new ArrayList<>();

    public Assinatura(String id, String clienteId, Topico topico) {
        this.id = id;
        this.clienteId = clienteId;
        this.topico = topico;
    }

    public String getId() { return id; }
    public String getClienteId() { return clienteId; }
    public Topico getTopico() { return topico; }

    public List<Evento> consultarEventos() {
        throw new UnsupportedOperationException("Ainda nao implementado");
    }
}
