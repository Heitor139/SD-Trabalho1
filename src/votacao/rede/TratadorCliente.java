package votacao.rede;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import votacao.modelo.Candidato;
import votacao.modelo.Eleitor;
import votacao.modelo.Mensagem;
import votacao.serial.Marshaller;
import votacao.serial.Requisicao;
import votacao.serial.Resposta;
import votacao.servico.ServicoCandidatos;
import votacao.servico.ServicoVotacao;

/**
 * Atende um cliente em sua propria thread: desempacota a requisicao,
 * executa a operacao no servico e empacota a resposta.
 */
public class TratadorCliente implements Runnable {

    private final Socket socket;
    private final ServicoCandidatos servicoCandidatos;
    private final ServicoVotacao servicoVotacao;
    private final NotificadorMulticast notificador;
    private Eleitor autenticado;

    public TratadorCliente(Socket socket, ServicoCandidatos servicoCandidatos,
                           ServicoVotacao servicoVotacao, NotificadorMulticast notificador) {
        this.socket = socket;
        this.servicoCandidatos = servicoCandidatos;
        this.servicoVotacao = servicoVotacao;
        this.notificador = notificador;
    }

    @Override
    public void run() {
        String origem = socket.getRemoteSocketAddress().toString();
        System.out.println("[servidor] conectado: " + origem);
        try (Socket aberto = socket) {
            InputStream entrada = aberto.getInputStream();
            OutputStream saida = aberto.getOutputStream();
            Object recebido;
            while ((recebido = Marshaller.receber(entrada)) != null) {
                Requisicao requisicao = (Requisicao) recebido;
                if (Protocolo.SAIR.equals(requisicao.getOperacao())) {
                    break;
                }
                Marshaller.enviar(saida, executar(requisicao));
            }
        } catch (IOException e) {
            System.err.println("[servidor] conexao encerrada (" + origem + "): " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("[servidor] requisicao invalida de " + origem + ": " + e.getMessage());
        }
        System.out.println("[servidor] desconectado: " + origem);
    }

    /** Despacha a operacao remota solicitada. */
    private Resposta executar(Requisicao requisicao) {
        switch (requisicao.getOperacao()) {
            case Protocolo.LOGIN:
                return login(requisicao);
            case Protocolo.LISTAR_CANDIDATOS:
                return listarCandidatos();
            case Protocolo.VOTAR:
                return votar(requisicao);
            case Protocolo.ENVIAR_NOTA:
                return enviarNota(requisicao);
            default:
                return Resposta.erro("operacao desconhecida: " + requisicao.getOperacao());
        }
    }

    private Resposta login(Requisicao requisicao) {
        Eleitor eleitor = servicoVotacao.autenticar(requisicao.texto("titulo"), requisicao.texto("senha"));
        if (eleitor == null) {
            return Resposta.erro("credenciais invalidas");
        }
        autenticado = eleitor;
        return Resposta.ok("bem-vindo, " + eleitor.getNome())
                .com("nome", eleitor.getNome())
                .com("jaVotou", eleitor.isJaVotou())
                .com("candidatos", candidatos());
    }

    private Resposta listarCandidatos() {
        if (autenticado == null) {
            return Resposta.erro("faca login primeiro");
        }
        return Resposta.ok("candidatos em votacao").com("candidatos", candidatos());
    }

    private Resposta votar(Requisicao requisicao) {
        if (autenticado == null) {
            return Resposta.erro("faca login primeiro");
        }
        ServicoVotacao.StatusVoto status =
                servicoVotacao.votar(autenticado.getTitulo(), requisicao.texto("numero"));
        if (status != ServicoVotacao.StatusVoto.ACEITO) {
            return Resposta.erro("voto recusado: " + status);
        }
        return Resposta.ok("voto computado").com("totalDeVotos", servicoVotacao.totalDeVotos());
    }

    /** Questao 4e: pedido chega por TCP, a nota sai por multicast UDP. */
    private Resposta enviarNota(Requisicao requisicao) {
        if (autenticado == null) {
            return Resposta.erro("faca login primeiro");
        }
        String tipo = requisicao.texto("tipo");
        try {
            notificador.enviar(tipo == null ? Mensagem.NOTIFICACAO : tipo, requisicao.texto("texto"));
        } catch (IOException e) {
            return Resposta.erro("falha no multicast: " + e.getMessage());
        }
        return Resposta.ok("nota enviada por multicast");
    }

    /** Lista de candidatos pronta para ser empacotada na resposta. */
    private ArrayList<Candidato> candidatos() {
        return new ArrayList<>(servicoCandidatos.listar());
    }
}
