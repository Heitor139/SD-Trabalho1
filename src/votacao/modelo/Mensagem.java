package votacao.modelo;

import java.io.Serializable;

/** POJO das notas informativas enviadas por multicast. */
public class Mensagem implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String NOTIFICACAO = "NOTIFICACAO";
    public static final String ALERTA = "ALERTA";
    public static final String ATUALIZACAO = "ATUALIZACAO";

    private String tipo;
    private String mensagem;
    private long timestamp;

    public Mensagem(String tipo, String mensagem) {
        this(tipo, mensagem, System.currentTimeMillis());
    }

    public Mensagem(String tipo, String mensagem, long timestamp) {
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.timestamp = timestamp;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "[" + tipo + "] " + mensagem;
    }
}
