package votacao.servico;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import votacao.modelo.Assinatura;
import votacao.modelo.Evento;
import votacao.modelo.Votacao;

public final class Intermediario implements Comunicacao {
    private final Map<String, Votacao> votacoes = new HashMap<>();
    private final Map<String, Assinatura> assinaturas = new HashMap<>();

    public Votacao cadastrarVotacao(Votacao votacao) {
        throw new UnsupportedOperationException("Ainda nao implementado");
    }

    public List<Votacao> listarVotacoes() {
        throw new UnsupportedOperationException("Ainda nao implementado");
    }

    public Assinatura assinar(String clienteId, String votacaoId) {
        throw new UnsupportedOperationException("Ainda nao implementado");
    }

    public void cancelarAssinatura(String assinaturaId) {
        throw new UnsupportedOperationException("Ainda nao implementado");
    }

    public void publicar(Evento evento) {
        throw new UnsupportedOperationException("Ainda nao implementado");
    }

    public List<Evento> consultarEventos(String assinaturaId) {
        throw new UnsupportedOperationException("Ainda nao implementado");
    }
}
