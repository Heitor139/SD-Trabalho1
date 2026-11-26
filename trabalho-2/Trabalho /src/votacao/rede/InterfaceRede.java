package votacao.rede;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;

public final class InterfaceRede {

    public static final String PROPRIEDADE = "votacao.interface";

    private static NetworkInterface escolhida;

    private InterfaceRede() {
    }

    public static synchronized NetworkInterface multicast() throws IOException {
        if (escolhida == null) {
            escolhida = procurar();
        }
        return escolhida;
    }

    private static NetworkInterface procurar() throws IOException {
        String forcada = System.getProperty(PROPRIEDADE);
        if (forcada != null && !forcada.isEmpty()) {
            NetworkInterface ni = NetworkInterface.getByName(forcada);
            if (ni == null) {
                throw new IOException("interface inexistente: " + forcada);
            }
            return ni;
        }

        NetworkInterface externa = null;
        for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (!ni.isUp() || !temIPv4(ni)) {
                continue;
            }
            if (ni.isLoopback()) {
                return ni;
            }
            if (externa == null && ni.supportsMulticast()) {
                externa = ni;
            }
        }
        if (externa == null) {
            throw new IOException("nenhuma interface IPv4 com suporte a multicast");
        }
        return externa;
    }

    private static boolean temIPv4(NetworkInterface ni) {
        for (InetAddress endereco : Collections.list(ni.getInetAddresses())) {
            if (endereco instanceof Inet4Address) {
                return true;
            }
        }
        return false;
    }
}
