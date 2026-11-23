package votacao.servico;

import java.util.List;
import votacao.modelo.Assinatura;
import votacao.modelo.Evento;
import votacao.modelo.Topico;

public interface Comunicacao {
    Assinatura registrarVotoConfirmado(String clienteId, String votacaoId);
    Assinatura assinar(String clienteId, Topico topico);
    void cancelarAssinatura(String assinaturaId);
    void publicar(Evento evento);
    List<Evento> consultarEventos(String assinaturaId);
}
