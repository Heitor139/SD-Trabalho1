package votacao.servico;

import java.util.List;
import votacao.modelo.Assinatura;
import votacao.modelo.Evento;
import votacao.modelo.Votacao;

public interface Comunicacao {
    Votacao cadastrarVotacao(Votacao votacao);
    List<Votacao> listarVotacoes();
    Assinatura assinar(String clienteId, String votacaoId);
    void cancelarAssinatura(String assinaturaId);
    void publicar(Evento evento);
    List<Evento> consultarEventos(String assinaturaId);
}
