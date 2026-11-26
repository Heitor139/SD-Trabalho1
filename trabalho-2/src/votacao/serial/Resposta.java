package votacao.serial;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Resposta implements Serializable {

    public static final String OK = "OK";
    public static final String ERRO = "ERRO";

    private final String status;
    private final String mensagem;
    private final LinkedHashMap<String, Serializable> dados = new LinkedHashMap<>();

    public Resposta(String status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
    }

    public static Resposta ok(String mensagem) {
        return new Resposta(OK, mensagem);
    }

    public static Resposta erro(String mensagem) {
        return new Resposta(ERRO, mensagem);
    }

    public Resposta com(String chave, Serializable valor) {
        dados.put(chave, valor);
        return this;
    }

    public boolean sucesso() {
        return OK.equals(status);
    }

    public String getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Map<String, Serializable> getDados() {
        return dados;
    }

    @Override
    public String toString() {
        return "Resposta{" + status + ", " + mensagem + ", dados=" + dados + "}";
    }
}
