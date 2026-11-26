package votacao.modelo;

import java.io.Serializable;

public record Topico(Tipo tipo, String id) implements Serializable {
    public enum Tipo {
        VOTACAO,
        NOTIFICACAO
    }

    public Topico {
        if (tipo == null || id == null || id.isBlank()) {
            throw new IllegalArgumentException("O tópico precisa de tipo e ID.");
        }
    }
}
