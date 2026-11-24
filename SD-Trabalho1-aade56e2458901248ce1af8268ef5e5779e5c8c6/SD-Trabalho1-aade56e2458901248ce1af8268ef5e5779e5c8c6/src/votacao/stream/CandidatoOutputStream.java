package votacao.stream;

import java.io.IOException;
import java.io.OutputStream;
import votacao.modelo.Candidato;

public class CandidatoOutputStream extends OutputStream {

    private final OutputStream destino;
    private final Candidato[] candidatos;
    private final int quantidade;

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

    public void enviarCandidatos() throws IOException {
        for (int i = 0; i < quantidade; i++) {
            write(serializar(candidatos[i]).getBytes("UTF-8")); // tranforma a string em bytes usando UTF-8
        }
        flush();
    }

    public static String serializar(Candidato candidato) {
        return candidato.getNumero() + ";"
                + candidato.getNome() + ";"
                + candidato.getPartido() + "\n";
    }
}
