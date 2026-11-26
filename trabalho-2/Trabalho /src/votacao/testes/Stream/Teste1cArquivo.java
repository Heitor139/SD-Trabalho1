package votacao.testes.Stream;

import java.io.File;
import java.io.FileOutputStream;
import votacao.app.DadosIniciais;
import votacao.modelo.Candidato;
import votacao.stream.CandidatoOutputStream;

public class Teste1cArquivo {

    public static final String ARQUIVO = "dados/candidatos.txt";

    public static void main(String[] args) throws Exception {
        Candidato[] candidatos = DadosIniciais.candidatos();
        File arquivo = new File(ARQUIVO);
        arquivo.getParentFile().mkdirs();

        System.out.println("== Questao 1c: destino = FileOutputStream ==");
        try (CandidatoOutputStream saida = new CandidatoOutputStream(
                new FileOutputStream(arquivo), candidatos, candidatos.length)) {
            saida.enviarCandidatos();
        }

        System.out.println("Arquivo gravado: " + arquivo.getPath()
                + " (" + arquivo.length() + " bytes)");
        System.out.println("Conteudo:");
        for (String linha : java.nio.file.Files.readAllLines(arquivo.toPath())) {
            System.out.println("  " + linha);
        }
        System.out.println("OK. " + candidatos.length + " registros gravados.");
    }
}
