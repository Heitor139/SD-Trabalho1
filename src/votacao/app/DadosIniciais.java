package votacao.app;

import votacao.modelo.Candidato;

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

}
