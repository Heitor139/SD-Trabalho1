package votacao.console;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

final class CaixaMensagens {
    private static final Pattern ID = Pattern.compile("[0-9]{13}-[a-f0-9-]{36}");
    private final Path raiz;

    CaixaMensagens(Path raiz) {
        this.raiz = raiz;
    }

    String enviar(String caixa, String[] comando) throws IOException {
        for (String argumento : comando) {
            if (argumento.contains("\n") || argumento.contains("\r")) {
                throw new IllegalArgumentException("Cada argumento deve caber em uma linha.");
            }
        }
        String id = System.currentTimeMillis() + "-" + UUID.randomUUID();
        gravar(raiz.resolve(caixa).resolve(id), String.join("\n", comando) + "\n");
        return id;
    }

    List<Path> pendentes(String caixa) throws IOException {
        Path pasta = Files.createDirectories(raiz.resolve(caixa));
        try (var arquivos = Files.list(pasta)) {
            return arquivos.filter(path -> ID.matcher(path.getFileName().toString()).matches())
                    .sorted().toList();
        }
    }

    void encaminhar(String caixa, Path mensagem) throws IOException {
        Path destino = Files.createDirectories(raiz.resolve(caixa)).resolve(mensagem.getFileName());
        Files.move(mensagem, destino, StandardCopyOption.ATOMIC_MOVE);
    }

    void registrar(String id, String resultado) throws IOException {
        gravar(raiz.resolve("recibos").resolve(id), resultado);
    }

    String resultado(String id) throws IOException {
        if (!ID.matcher(id).matches()) {
            throw new IllegalArgumentException("Identificador de comando inválido.");
        }
        Path arquivo = raiz.resolve("recibos").resolve(id);
        return Files.exists(arquivo) ? Files.readString(arquivo) : null;
    }

    private static void gravar(Path destino, String conteudo) throws IOException {
        Path pasta = Files.createDirectories(destino.getParent());
        Path temporario = Files.createTempFile(pasta, "mensagem-", ".tmp");
        try {
            Files.writeString(temporario, conteudo);
            Files.move(temporario, destino, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporario);
        }
    }
}
