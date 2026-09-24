package votacao.modelo;

import java.io.Serializable;

/** POJO que representa um voto computado. */
public class Voto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tituloEleitor;
    private String numeroCandidato;
    private long instante;

    public Voto(String tituloEleitor, String numeroCandidato) {
        this(tituloEleitor, numeroCandidato, System.currentTimeMillis());
    }

    public Voto(String tituloEleitor, String numeroCandidato, long instante) {
        this.tituloEleitor = tituloEleitor;
        this.numeroCandidato = numeroCandidato;
        this.instante = instante;
    }

    public String getTituloEleitor() {
        return tituloEleitor;
    }

    public String getNumeroCandidato() {
        return numeroCandidato;
    }

    public long getInstante() {
        return instante;
    }

    @Override
    public String toString() {
        return "Voto{eleitor=" + tituloEleitor + ", candidato=" + numeroCandidato + "}";
    }
}
