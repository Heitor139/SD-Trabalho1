package votacao.testes.Stream;

import java.io.FileInputStream;
import java.util.List;
import votacao.modelo.Candidato;
import votacao.stream.CandidatoInputStream;

/** Questao 2c: CandidatoInputStream tendo um arquivo como origem. */
public class Teste5ArquivoLeitura {

    public static void main(String[] args) throws Exception {
        String caminho = args.length > 0 ? args[0] : Teste1cArquivo.ARQUIVO;

        System.out.println("== Questao 2c: origem = FileInputStream ==");
        System.out.println("Lendo de " + caminho);

        try (CandidatoInputStream entrada = new CandidatoInputStream(new FileInputStream(caminho))) {
            List<Candidato> candidatos = entrada.lerTodos();
            for (Candidato candidato : candidatos) {
                System.out.println("  lido: " + candidato);
            }
            System.out.println("OK: " + candidatos.size() + " candidato(s) reconstruidos do arquivo.");
        }
    }
}
