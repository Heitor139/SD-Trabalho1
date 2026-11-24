package modelo;
import java.lang.Exception;

public class Candidato {
    private String nome = null;
    private Integer numero = null;
    private String partido = null;

    public Candidato(String nome, Integer numero, String partido) {
        this.nome = nome;
        this.numero = numero;
        this.partido = partido;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getNumero() {
        return  this.numero;
    }

    public void setNumero(Integer numero) throws IllegalArgumentException {
        if(numero < 0) throw new IllegalArgumentException("Invalid number");
        this.numero = numero;
    }

    public String getPartido() {
        return this.partido;
    }

    public void setPartido(String partido) {
        this.partido = partido;
    }
    
}
