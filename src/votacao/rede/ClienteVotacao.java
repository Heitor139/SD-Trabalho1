package votacao.rede;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import votacao.modelo.Candidato;
import votacao.serial.Marshaller;
import votacao.serial.Requisicao;
import votacao.serial.Resposta;

/**
 * Lado cliente do unicast TCP: empacota a requisicao, envia ao servidor e
 * desempacota a resposta recebida.
 */
public class ClienteVotacao implements AutoCloseable {

    private final Socket socket;
    private final OutputStream saida;
    private final InputStream entrada;

    public ClienteVotacao(String host, int porta) throws IOException {
        this.socket = new Socket(host, porta);
        this.saida = socket.getOutputStream();
        this.entrada = socket.getInputStream();
    }

    /** Envia a requisicao empacotada e devolve a resposta desempacotada. */
    public synchronized Resposta chamar(Requisicao requisicao) throws IOException {
        Marshaller.enviar(saida, requisicao);
        Object recebido = Marshaller.receber(entrada);
        if (recebido == null) {
            throw new IOException("servidor encerrou a conexao");
        }
        return (Resposta) recebido;
    }

    public Resposta login(String titulo, String senha) throws IOException {
        return chamar(new Requisicao(Protocolo.LOGIN).com("titulo", titulo).com("senha", senha));
    }

    public Resposta listarCandidatos() throws IOException {
        return chamar(new Requisicao(Protocolo.LISTAR_CANDIDATOS));
    }

    public Resposta votar(String numero) throws IOException {
        return chamar(new Requisicao(Protocolo.VOTAR).com("numero", numero));
    }

    public Resposta enviarNota(String tipo, String texto) throws IOException {
        return chamar(new Requisicao(Protocolo.ENVIAR_NOTA).com("tipo", tipo).com("texto", texto));
    }

    /** Extrai a lista de candidatos de uma resposta que a contenha. */
    @SuppressWarnings("unchecked")
    public static List<Candidato> candidatosDe(Resposta resposta) {
        Object candidatos = resposta.getDados().get("candidatos");
        return candidatos instanceof List
                ? (List<Candidato>) candidatos
                : new ArrayList<Candidato>();
    }

    @Override
    public void close() {
        try {
            Marshaller.enviar(saida, new Requisicao(Protocolo.SAIR));
        } catch (IOException ignorado) {
            // conexao ja caiu
        }
        try {
            socket.close();
        } catch (IOException ignorado) {
            // encerrando
        }
    }
}
