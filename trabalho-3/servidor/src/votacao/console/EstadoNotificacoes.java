package votacao.console;

import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;
import votacao.servico.Intermediario;

final class EstadoNotificacoes {
    private static final ObjectInputFilter CLASSES_DO_ESTADO = ObjectInputFilter.Config.createFilter(
            String.join(";",
                    "votacao.servico.Intermediario",
                    "votacao.console.EstadoNotificacoes$Registro",
                    "votacao.modelo.Assinatura",
                    "votacao.modelo.Topico",
                    "votacao.modelo.Topico$Tipo",
                    "votacao.modelo.Evento",
                    "votacao.modelo.Mensagem",
                    "votacao.modelo.Notificacao",
                    "votacao.modelo.Alerta",
                    "java.lang.Enum",
                    "java.lang.Object",
                    "java.util.HashMap",
                    "java.util.Map$Entry",
                    "java.util.ArrayList",
                    "!*"));

    private final Path diretorio;
    private final Path estado;

    EstadoNotificacoes(Path diretorio) {
        this.diretorio = diretorio;
        this.estado = diretorio.resolve("estado.bin");
    }

    String ultimoId() throws IOException {
        return carregar().id();
    }

    String executar(String id, String[] comando) throws IOException {
        Registro anterior = carregar();
        if (id.equals(anterior.id())) {
            return anterior.resultado();
        }
        Intermediario intermediario = anterior.intermediario();
        String resultado;
        try {
            intermediario.distribuirPendencias();
            resultado = ComandosNotificacoes.interpretar(comando).apply(intermediario);
            intermediario.distribuirPendencias();
        } catch (IllegalArgumentException | NoSuchElementException erro) {
            resultado = "Erro: " + erro.getMessage();
        }
        salvar(new Registro(intermediario, id, resultado));
        return resultado;
    }

    private record Registro(Intermediario intermediario, String id, String resultado)
            implements java.io.Serializable {
    }

    private Registro carregar() throws IOException {
        if (Files.notExists(estado)) {
            return new Registro(new Intermediario(), null, null);
        }
        try (var entrada = new ObjectInputStream(Files.newInputStream(estado))) {
            entrada.setObjectInputFilter(CLASSES_DO_ESTADO);
            Object salvo = entrada.readObject();
            if (salvo instanceof Registro registro) {
                return registro;
            }
            if (salvo instanceof Intermediario intermediario) {
                return new Registro(intermediario, null, null);
            }
            throw new IOException("O arquivo não contém o estado do intermediário.");
        } catch (ClassNotFoundException erro) {
            throw new IOException("Não foi possível ler o estado do intermediário.", erro);
        }
    }

    private void salvar(Registro registro) throws IOException {
        Path temporario = Files.createTempFile(diretorio, "estado-", ".tmp");
        try {
            try (var saida = new ObjectOutputStream(Files.newOutputStream(temporario))) {
                saida.writeObject(registro);
            }
            Files.move(temporario, estado, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporario);
        }
    }
}
