package votacao.rede;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;
import votacao.modelo.Candidato;
import votacao.stream.CandidatoInputStream;

/**
 * Servidor TCP de apoio as questoes 1d e 2d: recebe de um cliente remoto os
 * registros produzidos por CandidatoOutputStream e os le com
 * CandidatoInputStream.
 */
public class ServidorStreamCandidatos implements Runnable, AutoCloseable {

    private final ServerSocket servidor;
    private final Consumer<List<Candidato>> aoReceber;
    private volatile boolean ativo = true;

    public ServidorStreamCandidatos(int porta, Consumer<List<Candidato>> aoReceber) throws IOException {
        this.servidor = new ServerSocket(porta);
        this.aoReceber = aoReceber;
    }

    public int getPorta() {
        return servidor.getLocalPort();
    }

    public Thread iniciar() {
        Thread thread = new Thread(this, "servidor-stream");
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    @Override
    public void run() {
        while (ativo) {
            try (Socket cliente = servidor.accept();
                 CandidatoInputStream leitor = new CandidatoInputStream(cliente.getInputStream())) {
                aoReceber.accept(leitor.lerTodos());
            } catch (IOException e) {
                if (ativo) {
                    System.err.println("[servidor-stream] " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void close() {
        ativo = false;
        try {
            servidor.close();
        } catch (IOException ignorado) {
            // encerrando
        }
    }
}
