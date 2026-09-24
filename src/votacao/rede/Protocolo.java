package votacao.rede;

/** Constantes do protocolo: portas, grupo multicast e nomes das operacoes. */
public final class Protocolo {

    private Protocolo() {
    }

    /** Porta TCP (unicast) do servidor de votacao. */
    public static final int PORTA_TCP = 5000;

    /** Porta TCP usada nos testes de stream remoto (questoes 1d e 2d). */
    public static final int PORTA_TCP_STREAM = 5001;

    /** Endereco IP classe D do grupo multicast. */
    public static final String GRUPO_MULTICAST = "230.0.0.1";

    /** Porta fixa do grupo multicast. */
    public static final int PORTA_MULTICAST = 6000;

    // Operacoes remotas
    public static final String LOGIN = "LOGIN";
    public static final String LISTAR_CANDIDATOS = "LISTAR_CANDIDATOS";
    public static final String VOTAR = "VOTAR";
    public static final String ENVIAR_NOTA = "ENVIAR_NOTA";
    public static final String SAIR = "SAIR";
}
