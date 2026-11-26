package votacao.rede;

public final class Protocolo {

    private Protocolo() {
    }

    public static final int PORTA_TCP = 5000;

    public static final int PORTA_AUTENTICACAO = 5002;

    public static final int PORTA_TCP_STREAM = 5001;

    public static final String GRUPO_MULTICAST = "230.0.0.1";

    public static final int PORTA_MULTICAST = 6000;

    public static final String LOGIN = "LOGIN";
    public static final String LISTAR_CANDIDATOS = "LISTAR_CANDIDATOS";
    public static final String VOTAR = "VOTAR";
    public static final String ENVIAR_NOTA = "ENVIAR_NOTA";
    public static final String SAIR = "SAIR";
}
