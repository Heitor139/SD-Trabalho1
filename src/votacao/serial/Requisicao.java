package votacao.serial;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Requisicao implements Serializable {

    private final String operacao;
    private final LinkedHashMap<String, Serializable> argumentos = new LinkedHashMap<>();

    public Requisicao(String operacao) {
        this.operacao = operacao;
    }

    public Requisicao com(String chave, Serializable valor) {
        argumentos.put(chave, valor);
        return this;
    }

    public String getOperacao() {
        return operacao;
    }

    public Map<String, Serializable> getArgumentos() {
        return argumentos;
    }

    public String texto(String chave) {
        Serializable valor = argumentos.get(chave);
        return valor == null ? null : String.valueOf(valor);
    }

    @Override
    public String toString() {
        return "Requisicao{" + operacao + ", argumentos=" + argumentos + "}";
    }
}
