package votacao.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import votacao.modelo.Candidato;

public class CandidatoInputStream extends InputStream {

    private final InputStream origem;

    public CandidatoInputStream(InputStream origem) {
        if (origem == null) {
            throw new IllegalArgumentException("origem nao pode ser nula");
        }
        this.origem = origem;
    }

    @Override
    public int read() throws IOException {
        return origem.read();
    }

    @Override
    public void close() throws IOException {
        origem.close();
    }

    private String lerLinha() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;
        while ((b = read()) != -1) {
            if (b == '\n') {
                return new String(buffer.toByteArray(), "UTF-8");
            }
            if (b != '\r') {
                buffer.write(b);
            }
        }
        return buffer.size() == 0 ? null : new String(buffer.toByteArray(), "UTF-8");
    }
    
    public Candidato lerCandidato() throws IOException {
        String linha = lerLinha();
        while (linha != null && linha.trim().isEmpty()) { // enquanto a linha for nula ou vazia, lê a próxima linha
            linha = lerLinha();
        }
        if (linha == null) {
            return null;
        }
        return desserializar(linha);
    }

    public List<Candidato> lerTodos() throws IOException {
        List<Candidato> lidos = new ArrayList<>();
        Candidato candidato;
        while ((candidato = lerCandidato()) != null) {
            lidos.add(candidato);
        }
        return lidos;
    }

    public static Candidato desserializar(String linha) {
        String[] campos = linha.split(";", -1);
        if (campos.length < 2) {
            throw new IllegalArgumentException("registro invalido: " + linha);
        }
        String partido = campos.length > 2 ? campos[2] : "SEM PARTIDO";
        return new Candidato(campos[1], campos[0], partido);
    }
}
