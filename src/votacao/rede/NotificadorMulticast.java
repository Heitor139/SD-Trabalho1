package votacao.rede;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.InetAddress;
import votacao.modelo.Mensagem;
import votacao.serial.Marshaller;

/**
 * Questao 4: lado servidor do multicast. Envia notas informativas (UDP) para
 * o grupo classe D. E seguro para uso concorrente por varias threads.
 *
 * <p>A nota viaja empacotada pelo {@link Marshaller}, ou seja, no mesmo
 * formato binario de serializacao usado nas mensagens TCP.</p>
 */
public class NotificadorMulticast implements AutoCloseable {

    private final MulticastSocket socket;
    private final InetAddress grupo;
    private final int porta;

    public NotificadorMulticast() throws IOException {
        this(Protocolo.GRUPO_MULTICAST, Protocolo.PORTA_MULTICAST);
    }

    public NotificadorMulticast(String grupo, int porta) throws IOException {
        this.socket = new MulticastSocket();
        this.socket.setNetworkInterface(InterfaceRede.multicast());
        this.socket.setTimeToLive(1);
        this.grupo = InetAddress.getByName(grupo);
        this.porta = porta;
    }

    /** Empacota a mensagem (serializacao) e a envia para todo o grupo. */
    public synchronized void enviar(Mensagem mensagem) throws IOException {
        byte[] dados = Marshaller.empacotar(mensagem);
        socket.send(new DatagramPacket(dados, dados.length, grupo, porta));
    }

    public void enviar(String tipo, String texto) throws IOException {
        enviar(new Mensagem(tipo, texto));
    }

    @Override
    public void close() {
        socket.close();
    }
}
