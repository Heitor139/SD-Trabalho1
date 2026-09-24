package votacao.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Questao 3: representacao externa de dados usando a serializacao padrao do
 * Java ({@link Serializable}).
 *
 * <p>O empacotamento (marshalling) converte o objeto em uma sequencia de
 * bytes com {@link ObjectOutputStream}; o desempacotamento (unmarshalling)
 * reconstroi o objeto com {@link ObjectInputStream}. Cliente e servidor usam
 * exatamente os mesmos dois metodos, cada um em seu lado da conexao.</p>
 *
 * <p>No socket TCP cada pacote vai precedido do seu tamanho em 4 bytes, para
 * que o receptor saiba onde termina uma mensagem e comeca a proxima.</p>
 */
public final class Marshaller {

    private Marshaller() {
    }

    // ------------------------------------------------------------------
    // Empacotamento / desempacotamento
    // ------------------------------------------------------------------

    /** Empacota o objeto em uma sequencia de bytes. */
    public static byte[] empacotar(Serializable objeto) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream saida = new ObjectOutputStream(bytes)) {
            saida.writeObject(objeto);
        }
        return bytes.toByteArray();
    }

    /** Desempacota a sequencia de bytes, reconstruindo o objeto original. */
    public static Object desempacotar(byte[] pacote) throws IOException {
        try (ObjectInputStream entrada =
                     new ObjectInputStream(new ByteArrayInputStream(pacote))) {
            return entrada.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("classe desconhecida no pacote recebido", e);
        }
    }

    // ------------------------------------------------------------------
    // Transporte pelo socket TCP (tamanho + bytes)
    // ------------------------------------------------------------------

    /** Empacota o objeto e o envia pelo fluxo de bytes do socket. */
    public static void enviar(OutputStream destino, Serializable objeto) throws IOException {
        byte[] pacote = empacotar(objeto);
        DataOutputStream saida = new DataOutputStream(destino);
        saida.writeInt(pacote.length);
        saida.write(pacote);
        saida.flush();
    }

    /**
     * Le um pacote do fluxo de bytes e o desempacota.
     *
     * @return o objeto recebido ou {@code null} no fim do stream
     */
    public static Object receber(InputStream origem) throws IOException {
        DataInputStream entrada = new DataInputStream(origem);
        int tamanho;
        try {
            tamanho = entrada.readInt();
        } catch (EOFException fim) {
            return null;
        }
        if (tamanho <= 0) {
            throw new IOException("pacote com tamanho invalido: " + tamanho);
        }
        byte[] pacote = new byte[tamanho];
        entrada.readFully(pacote);
        return desempacotar(pacote);
    }
}
