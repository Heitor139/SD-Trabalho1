package votacao.modelo;

import java.io.Serializable;

public record Evento(Topico topico, Mensagem mensagem) implements Serializable {
    public Evento {
        if (topico == null || mensagem == null) {
            throw new IllegalArgumentException("O evento precisa de tópico e mensagem.");
        }
    }
}
