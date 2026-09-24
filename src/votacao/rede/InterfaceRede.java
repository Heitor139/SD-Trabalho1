package votacao.rede;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;

/**
 * Escolhe a interface de rede usada pelo multicast. Emissor e receptor
 * precisam usar a mesma interface; por isso a escolha fica centralizada.
 *
 * Pode ser fixada com {@code -Dvotacao.interface=lo}.
 */
public final class InterfaceRede {

    /** Propriedade de sistema para forcar uma interface especifica. */
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

        // Por padrao usa-se o loopback: todos os processos da demonstracao
        // rodam na mesma maquina. Para uma demonstracao entre maquinas,
        // informe a interface fisica em -Dvotacao.interface=<nome>.
        NetworkInterface externa = null;
        for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (!ni.isUp() || !temIPv4(ni)) {
                continue;
            }
            if (ni.isLoopback()) {
                // o loopback do Linux nao anuncia a flag MULTICAST, mas
                // entrega os datagramas do grupo entre processos locais
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

    /** O grupo 230.0.0.1 e IPv4: a interface precisa ter endereco IPv4. */
    private static boolean temIPv4(NetworkInterface ni) {
        for (InetAddress endereco : Collections.list(ni.getInetAddresses())) {
            if (endereco instanceof Inet4Address) {
                return true;
            }
        }
        return false;
    }
}
