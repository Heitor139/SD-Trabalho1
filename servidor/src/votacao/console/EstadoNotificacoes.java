package votacao.console;

import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;
import votacao.servico.Comunicacao;
import votacao.servico.Intermediario;

final class EstadoNotificacoes {
    private static final ObjectInputFilter CLASSES_DO_ESTADO = ObjectInputFilter.Config.createFilter(
            String.join(";",
                    "votacao.servico.Intermediario",
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

    String executar(Function<Comunicacao, String> operacao) throws IOException {
        Files.createDirectories(diretorio);
        Path controle = diretorio.resolve("estado.lock");
        try (var canal = FileChannel.open(controle, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            canal.lock();
            Intermediario intermediario = carregar();
            String resultado = operacao.apply(intermediario);
            salvar(intermediario);
            return resultado;
        }
    }

    private Intermediario carregar() throws IOException {
        if (Files.notExists(estado)) {
            return new Intermediario();
        }
        try (var entrada = new ObjectInputStream(Files.newInputStream(estado))) {
            entrada.setObjectInputFilter(CLASSES_DO_ESTADO);
            if (entrada.readObject() instanceof Intermediario intermediario) {
                return intermediario;
            }
            throw new IOException("O arquivo não contém o estado do intermediário.");
        } catch (ClassNotFoundException erro) {
            throw new IOException("Não foi possível ler o estado do intermediário.", erro);
        }
    }

    private void salvar(Intermediario intermediario) throws IOException {
        Path temporario = Files.createTempFile(diretorio, "estado-", ".tmp");
        try {
            try (var saida = new ObjectOutputStream(Files.newOutputStream(temporario))) {
                saida.writeObject(intermediario);
            }
            Files.move(temporario, estado, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporario);
        }
    }
}
