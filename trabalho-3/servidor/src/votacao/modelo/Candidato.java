package votacao.modelo;

import java.io.Serializable;

public class Candidato implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String numero;
    private String partido;

    public Candidato(String nome, String numero) {
        this(nome, numero, "SEM PARTIDO");
    }

    public Candidato(String nome, String numero, String partido) {
        this.nome = nome;
        this.numero = numero;
        this.partido = partido;
    }

    public String getNome() {
        return nome;
    }

    public String getNumero() {
        return numero;
    }

    public String getPartido() {
        return partido;
    }

    @Override
    public String toString() {
        return numero + " - " + nome + " (" + partido + ")";
    }
}
