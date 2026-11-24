package votacao.modelo;

public final class Notificacao extends Mensagem {
    private static final long serialVersionUID = 1L;

    public Notificacao(String mensagem) {
        super(NOTIFICACAO, mensagem);
    }
}
