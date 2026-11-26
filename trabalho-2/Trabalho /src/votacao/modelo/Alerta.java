package votacao.modelo;

public final class Alerta extends Mensagem {
    private static final long serialVersionUID = 1L;

    public Alerta(String mensagem) {
        super(ALERTA, mensagem);
    }
}
