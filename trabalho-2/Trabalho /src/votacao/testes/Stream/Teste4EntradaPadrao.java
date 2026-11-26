package votacao.testes.Stream;

import votacao.modelo.Candidato;
import votacao.stream.CandidatoInputStream;

public class Teste4EntradaPadrao {

    public static void main(String[] args) throws Exception {
        System.out.println("== Questao 2b: origem = System.in ==");

        CandidatoInputStream entrada = new CandidatoInputStream(System.in);
        int total = 0;
        Candidato candidato;
        while ((candidato = entrada.lerCandidato()) != null) {
            System.out.println("  lido: " + candidato);
            total++;
        }

        System.out.println("OK: " + total + " candidato(s) lidos da entrada padrao.");
    }
}
