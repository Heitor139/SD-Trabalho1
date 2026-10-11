package votacao.testes.Stream;

import votacao.app.DadosIniciais;
import votacao.modelo.Candidato;
import votacao.stream.CandidatoOutputStream;

public class Teste1bSaidaPadrao {

    public static void main(String[] args) throws Exception {
        Candidato[] candidatos = DadosIniciais.candidatos();

        System.out.println("== Questao 1b: destino = System.out ==");
        System.out.println("Enviando " + candidatos.length + " candidatos:");

        try (CandidatoOutputStream saida =
                     new CandidatoOutputStream(System.out, candidatos, candidatos.length)) {
            saida.enviarCandidatos();
        }

        System.out.println("OK. registros escritos na saida padrao.");
    }
}
