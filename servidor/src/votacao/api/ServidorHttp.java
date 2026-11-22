package votacao.api;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import votacao.servico.Intermediario;

public final class ServidorHttp {
    private ServidorHttp() {
    }

    public static void main(String[] args) throws IOException {
        throw new UnsupportedOperationException("Ainda nao implementado");
    }

    public static HttpServer criar(InetSocketAddress endereco, Intermediario intermediario) {
        throw new UnsupportedOperationException("Ainda nao implementado");
    }
}
