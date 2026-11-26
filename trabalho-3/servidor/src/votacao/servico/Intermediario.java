package votacao.servico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import votacao.modelo.Assinatura;
import votacao.modelo.Evento;
import votacao.modelo.Topico;

public final class Intermediario implements Comunicacao, Serializable {
    private static final long serialVersionUID = 1L;

    private final HashMap<String, Assinatura> assinaturas = new HashMap<>();
    private final HashMap<Topico, Map<String, Assinatura>> assinaturasPorTopico = new HashMap<>();

    private ArrayList<Evento> publicacoesPendentes = new ArrayList<>();

    @Override
    public synchronized Assinatura registrarVotoConfirmado(String clienteId, String votacaoId) {
        return criarAssinatura(clienteId, new Topico(Topico.Tipo.VOTACAO, votacaoId));
    }

    @Override
    public synchronized Assinatura assinar(String clienteId, Topico topico) {
        if (topico.tipo() != Topico.Tipo.NOTIFICACAO) {
            throw new IllegalArgumentException("Assinaturas de votação são criadas com a confirmação do voto.");
        }
        return criarAssinatura(clienteId, topico);
    }

    @Override
    public synchronized void cancelarAssinatura(String assinaturaId) {
        Assinatura assinatura = obterAssinatura(assinaturaId);
        Map<String, Assinatura> assinantes = assinaturasPorTopico.get(assinatura.getTopico());
        assinantes.remove(assinatura.getClienteId());
        if (assinantes.isEmpty()) {
            assinaturasPorTopico.remove(assinatura.getTopico());
        }
        assinaturas.remove(assinaturaId);
    }

    @Override
    public synchronized void publicar(Evento evento) {
        if (publicacoesPendentes == null) {
            publicacoesPendentes = new ArrayList<>();
        }
        publicacoesPendentes.add(evento);
    }

    public synchronized boolean temPublicacoesPendentes() {
        return publicacoesPendentes != null && !publicacoesPendentes.isEmpty();
    }

    public synchronized void distribuirPendencias() {
        if (!temPublicacoesPendentes()) {
            return;
        }
        for (Evento evento : publicacoesPendentes) {
            Map<String, Assinatura> assinantes =
                    assinaturasPorTopico.getOrDefault(evento.topico(), Map.of());
            for (Assinatura assinatura : assinantes.values()) {
                assinatura.receber(evento);
            }
        }
        publicacoesPendentes.clear();
    }

    @Override
    public synchronized List<Evento> consultarEventos(String assinaturaId) {
        return obterAssinatura(assinaturaId).consultarEventos();
    }

    private Assinatura criarAssinatura(String clienteId, Topico topico) {
        if (clienteId == null || clienteId.isBlank()) {
            throw new IllegalArgumentException("Informe o identificador da pessoa.");
        }
        Map<String, Assinatura> assinantes =
                assinaturasPorTopico.computeIfAbsent(topico, chave -> new HashMap<>());
        return assinantes.computeIfAbsent(clienteId, cliente -> {
            Assinatura assinatura = new Assinatura(UUID.randomUUID().toString(), cliente, topico);
            assinaturas.put(assinatura.getId(), assinatura);
            return assinatura;
        });
    }

    private Assinatura obterAssinatura(String id) {
        Assinatura assinatura = assinaturas.get(id);
        if (assinatura == null) {
            throw new NoSuchElementException("Assinatura inexistente.");
        }
        return assinatura;
    }
}
