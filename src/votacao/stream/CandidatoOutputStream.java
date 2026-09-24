package votacao.stream;

import java.io.IOException;
import java.io.OutputStream;
import votacao.modelo.Candidato;

/**
 * Questao 1: subclasse de OutputStream que envia os dados de um array de
 * candidatos para um OutputStream de destino qualquer (System.out, arquivo
 * ou socket TCP).
 *
 * Formato de cada registro (uma linha por candidato):
 * {@code numero;nome;partido\n}
 */
public class CandidatoOutputStream extends OutputStream {

    public static final String SEPARADOR = ";";

    private final OutputStream destino;
    private final Candidato[] candidatos;
    private final int quantidade;

    /**
     * @param destino     stream para onde os dados serao enviados
     * @param candidatos  array de objetos a transmitir
     * @param quantidade  quantos objetos do array serao enviados
     */
    public CandidatoOutputStream(OutputStream destino, Candidato[] candidatos, int quantidade) {
        if (destino == null || candidatos == null) {
            throw new IllegalArgumentException("destino e candidatos nao podem ser nulos");
        }
        if (quantidade < 0 || quantidade > candidatos.length) {
            throw new IllegalArgumentException("quantidade fora dos limites do array");
        }
        this.destino = destino;
        this.candidatos = candidatos;
        this.quantidade = quantidade;
    }

    @Override
    public void write(int b) throws IOException {
        destino.write(b);
    }

    @Override
    public void flush() throws IOException {
        destino.flush();
    }

    /** Serializa e envia ao destino os {@code quantidade} primeiros candidatos. */
    public void enviarCandidatos() throws IOException {
        for (int i = 0; i < quantidade; i++) {
            write(serializar(candidatos[i]).getBytes("UTF-8"));
        }
        flush();
    }

    /** Converte um candidato no registro textual trafegado pelo stream. */
    public static String serializar(Candidato candidato) {
        return candidato.getNumero() + SEPARADOR
                + candidato.getNome() + SEPARADOR
                + candidato.getPartido() + "\n";
    }
}
