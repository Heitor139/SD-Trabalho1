package votacao.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import votacao.servico.Intermediario;

public final class AssinaturasApi implements HttpHandler {
    private final Intermediario intermediario;

    public AssinaturasApi(Intermediario intermediario) {
        this.intermediario = intermediario;
    }

    @Override
    public void handle(HttpExchange requisicao) throws IOException {
        throw new UnsupportedOperationException("Ainda nao implementado");
    }
}
