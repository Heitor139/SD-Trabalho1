package votacao.rede;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.function.Consumer;
import votacao.modelo.Mensagem;
import votacao.serial.Marshaller;

public final class OuvinteMulticast extends Thread {

    private final MulticastSocket socket;
    private final InetSocketAddress enderecoGrupo;
    private final NetworkInterface interfaceRede;
    private final Consumer<Mensagem> aoReceber;
    private volatile boolean ativo = true;

    public OuvinteMulticast(Consumer<Mensagem> aoReceber) throws IOException {
        this(Protocolo.GRUPO_MULTICAST, Protocolo.PORTA_MULTICAST, aoReceber);
    }

    public OuvinteMulticast(String grupo, int porta, Consumer<Mensagem> aoReceber) throws IOException {
        super("ouvinte-multicast");
        setDaemon(true);
        this.aoReceber = aoReceber;
        this.socket = new MulticastSocket(porta);
        this.socket.setReuseAddress(true);
        this.enderecoGrupo = new InetSocketAddress(InetAddress.getByName(grupo), porta);
        this.interfaceRede = InterfaceRede.multicast();
    }

    public void entrarNoGrupo() throws IOException {
        socket.joinGroup(enderecoGrupo, interfaceRede);
    }

    public void sairDoGrupo() {
        ativo = false;
        try {
            socket.leaveGroup(enderecoGrupo, interfaceRede);
        } catch (IOException ignorado) {
            // socket ja encerrado
        }
        socket.close();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[4096];
        while (ativo) {
            try {
                DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
                socket.receive(pacote);
                byte[] recebidos = java.util.Arrays.copyOf(pacote.getData(), pacote.getLength());
                aoReceber.accept((Mensagem) Marshaller.desempacotar(recebidos));
            } catch (IOException e) {
                if (ativo) {
                    System.err.println("[multicast] erro ao receber: " + e.getMessage());
                }
            }
        }
    }

}
