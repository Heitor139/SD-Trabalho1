package votacao.servico;

import java.util.ArrayList;
import java.util.List;
import votacao.modelo.Candidato;

/**
 * Classe de servico 1: cadastro dos candidatos em votacao (inclusao,
 * remocao e consulta).
 */
public class ServicoCandidatos {

    private final List<Candidato> candidatos = new ArrayList<>();

    public synchronized boolean adicionar(Candidato candidato) {
        if (buscarPorNumero(candidato.getNumero()) != null) {
            return false;
        }
        return candidatos.add(candidato);
    }

    public synchronized boolean remover(String numero) {
        Candidato alvo = buscarPorNumero(numero);
        return alvo != null && candidatos.remove(alvo);
    }

    public synchronized Candidato buscarPorNumero(String numero) {
        for (Candidato c : candidatos) {
            if (c.getNumero().equals(numero)) {
                return c;
            }
        }
        return null;
    }

    public synchronized List<Candidato> listar() {
        return new ArrayList<>(candidatos);
    }

    public synchronized Candidato[] paraArray() {
        return candidatos.toArray(new Candidato[0]);
    }

    public synchronized int quantidade() {
        return candidatos.size();
    }
}
