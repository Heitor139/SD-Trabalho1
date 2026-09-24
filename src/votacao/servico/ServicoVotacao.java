package votacao.servico;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import votacao.modelo.Eleitor;
import votacao.modelo.Voto;

/**
 * Classe de servico 2: autenticacao dos eleitores e registro dos votos.
 * Todos os metodos sao sincronizados porque o servidor e multithreading.
 */
public class ServicoVotacao {

    /** Resultado possivel de uma tentativa de voto. */
    public enum StatusVoto {
        ACEITO, ELEITOR_INVALIDO, JA_VOTOU, CANDIDATO_INEXISTENTE
    }

    private final ServicoCandidatos servicoCandidatos;
    private final Map<String, Eleitor> eleitores = new LinkedHashMap<>();
    private final List<Voto> votos = new ArrayList<>();

    public ServicoVotacao(ServicoCandidatos servicoCandidatos) {
        this.servicoCandidatos = servicoCandidatos;
    }

    public synchronized void cadastrar(Eleitor eleitor) {
        eleitores.put(eleitor.getTitulo(), eleitor);
    }

    /** Autentica um eleitor; devolve null quando as credenciais nao conferem. */
    public synchronized Eleitor autenticar(String titulo, String senha) {
        Eleitor eleitor = eleitores.get(titulo);
        if (eleitor != null && eleitor.getSenha().equals(senha)) {
            return eleitor;
        }
        return null;
    }

    public synchronized StatusVoto votar(String tituloEleitor, String numeroCandidato) {
        Eleitor eleitor = eleitores.get(tituloEleitor);
        if (eleitor == null) {
            return StatusVoto.ELEITOR_INVALIDO;
        }
        if (eleitor.isJaVotou()) {
            return StatusVoto.JA_VOTOU;
        }
        if (servicoCandidatos.buscarPorNumero(numeroCandidato) == null) {
            return StatusVoto.CANDIDATO_INEXISTENTE;
        }
        votos.add(new Voto(tituloEleitor, numeroCandidato));
        eleitor.marcarComoVotante();
        return StatusVoto.ACEITO;
    }

    public synchronized int totalDeVotos() {
        return votos.size();
    }

    public synchronized List<Voto> listarVotos() {
        return new ArrayList<>(votos);
    }
}
