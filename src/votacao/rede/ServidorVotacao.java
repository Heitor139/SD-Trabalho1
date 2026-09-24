package votacao.rede;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import votacao.servico.ServicoCandidatos;
import votacao.servico.ServicoVotacao;

/**
 * Servidor multithreading: aceita conexoes TCP (unicast) e delega cada
 * cliente a uma thread do pool. As notas informativas saem por multicast.
 */
public class ServidorVotacao implements Runnable, AutoCloseable {

    private final ServicoCandidatos servicoCandidatos;
    private final ServicoVotacao servicoVotacao;
    private final NotificadorMulticast notificador;
    private final ServerSocket servidor;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private volatile boolean ativo = true;

    public ServidorVotacao(int porta, ServicoCandidatos servicoCandidatos,
                           ServicoVotacao servicoVotacao) throws IOException {
        this.servicoCandidatos = servicoCandidatos;
        this.servicoVotacao = servicoVotacao;
        this.notificador = new NotificadorMulticast();
        this.servidor = new ServerSocket(porta);
    }

    public int getPorta() {
        return servidor.getLocalPort();
    }

    /** Inicia o laco de aceitacao em uma thread separada. */
    public Thread iniciar() {
        Thread thread = new Thread(this, "servidor-votacao");
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    @Override
    public void run() {
        System.out.println("[servidor] TCP em " + getPorta()
                + " | multicast " + Protocolo.GRUPO_MULTICAST + ":" + Protocolo.PORTA_MULTICAST);
        while (ativo) {
            try {
                Socket cliente = servidor.accept();
                pool.execute(new TratadorCliente(cliente, servicoCandidatos, servicoVotacao, notificador));
            } catch (IOException e) {
                if (ativo) {
                    System.err.println("[servidor] erro ao aceitar conexao: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void close() {
        ativo = false;
        pool.shutdownNow();
        notificador.close();
        try {
            servidor.close();
        } catch (IOException ignorado) {
            // encerrando
        }
    }
}
