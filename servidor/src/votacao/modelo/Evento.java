package votacao.modelo;

import java.io.Serializable;

public record Evento(Topico topico, Mensagem mensagem) implements Serializable {
}
