import java.io.FileInputStream;

import modelo.Candidato;

public class main {
    public static void main(String[] args) {
        try{
            CandidatoInputStream input = new CandidatoInputStream(System.in);
            input.read();
            input.close();
            

            CandidatoInputStream input2 = null;
            input2 = new CandidatoInputStream(new FileInputStream("text.txt"));
            input2.read();
            input2.close();
        

            Candidato cand1 = input.getCandidato().getFirst();
            Candidato cand2 = input2.getCandidato().getFirst();

            System.out.println(cand1.getNome());
            System.out.println(cand1.getNumero());
            System.out.println(cand1.getPartido());
            System.out.println(cand2.getNome());
            System.out.println(cand2.getNumero());
            System.out.println(cand2.getPartido());

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}