package votacao.modelo;

import java.io.Serializable;

/** POJO que representa um eleitor do sistema. */
public class Eleitor implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String titulo;
    private String senha;
    private boolean jaVotou;

    public Eleitor(String nome, String titulo, String senha) {
        this.nome = nome;
        this.titulo = titulo;
        this.senha = senha;
        this.jaVotou = false;
    }

    public String getNome() {
        return nome;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSenha() {
        return senha;
    }

    public boolean isJaVotou() {
        return jaVotou;
    }

    public void marcarComoVotante() {
        this.jaVotou = true;
    }

    @Override
    public String toString() {
        return titulo + " - " + nome;
    }
}
