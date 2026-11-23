package votacao.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Assinatura implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String clienteId;
    private final Topico topico;
    private final ArrayList<Evento> eventosPendentes = new ArrayList<>();

    public Assinatura(String id, String clienteId, Topico topico) {
        this.id = id;
        this.clienteId = clienteId;
        this.topico = topico;
    }

    public String getId() { return id; }
    public String getClienteId() { return clienteId; }
    public Topico getTopico() { return topico; }

    public synchronized void receber(Evento evento) {
        eventosPendentes.add(evento);
    }

    public synchronized List<Evento> consultarEventos() {
        List<Evento> eventos = List.copyOf(eventosPendentes);
        eventosPendentes.clear();
        return eventos;
    }
}
