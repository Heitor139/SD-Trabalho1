package votacao.app;

import votacao.modelo.Candidato;
import votacao.modelo.Eleitor;
import votacao.servico.ServicoCandidatos;
import votacao.servico.ServicoVotacao;

/** Carga inicial de candidatos e eleitores usada pelo servidor e pelos testes. */
public final class DadosIniciais {

    private DadosIniciais() {
    }

    public static Candidato[] candidatos() {
        return new Candidato[] {
            new Candidato("Ana Lima", "10", "PARTIDO A"),
            new Candidato("Bruno Souza", "20", "PARTIDO B"),
            new Candidato("Carla Dias", "30", "PARTIDO C"),
            new Candidato("Diego Rocha", "40", "PARTIDO D")
        };
    }

    public static ServicoCandidatos servicoCandidatos() {
        ServicoCandidatos servico = new ServicoCandidatos();
        for (Candidato candidato : candidatos()) {
            servico.adicionar(candidato);
        }
        return servico;
    }

    public static void cadastrarEleitores(ServicoVotacao servico) {
        servico.cadastrar(new Eleitor("Eleitor Um", "1111", "123"));
        servico.cadastrar(new Eleitor("Eleitor Dois", "2222", "123"));
        servico.cadastrar(new Eleitor("Eleitor Tres", "3333", "123"));
        servico.cadastrar(new Eleitor("Eleitor Quatro", "4444", "123"));
    }
}
