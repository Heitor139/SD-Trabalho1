package votacao.console;

import java.io.IOException;
import java.nio.file.Path;

public final class ServidorVotacao {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Uso: ServidorVotacao <diretório de mensagens>");
        }
        var caixas = new CaixaMensagens(Path.of(args[0]));
        System.out.println("Servidor de votação pronto.");
        while (true) {
            for (Path mensagem : caixas.pendentes("votos")) {
                caixas.encaminhar("intermediario", mensagem);
            }
            Thread.sleep(250);
        }
    }
}
