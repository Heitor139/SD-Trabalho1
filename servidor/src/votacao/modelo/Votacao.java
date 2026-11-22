package votacao.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Votacao implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String nome;
    private final List<Candidato> candidatos = new ArrayList<>();
    private final List<Eleitor> eleitores = new ArrayList<>();

    public Votacao(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }

    public void adicionarCandidato(Candidato candidato) { candidatos.add(candidato); }
    public void adicionarEleitor(Eleitor eleitor) { eleitores.add(eleitor); }
    public List<Candidato> getCandidatos() { return new ArrayList<>(candidatos); }
    public List<Eleitor> getEleitores() { return new ArrayList<>(eleitores); }
}
