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

public final class Marshaller {

    private Marshaller() {
    }

    public static byte[] empacotar(Serializable objeto) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream saida = new ObjectOutputStream(bytes)) {
            saida.writeObject(objeto);
        }
        return bytes.toByteArray();
    }

    public static Object desempacotar(byte[] pacote) throws IOException {
        try (ObjectInputStream entrada =
                     new ObjectInputStream(new ByteArrayInputStream(pacote))) {
            return entrada.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("classe desconhecida no pacote recebido", e);
        }
    }

    public static void enviar(OutputStream destino, Serializable objeto) throws IOException {
        byte[] pacote = empacotar(objeto);
        DataOutputStream saida = new DataOutputStream(destino);  // [tamanho] [dados]
        saida.writeInt(pacote.length);
        saida.write(pacote);
        saida.flush();
    }

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
