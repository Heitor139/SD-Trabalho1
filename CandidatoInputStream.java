import java.lang.Exception;
import modelo.*;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CandidatoInputStream extends InputStream {
    private InputStream origem = null;
    private ArrayList<Candidato> cand = new ArrayList<Candidato>();

    public CandidatoInputStream(InputStream origem) {
        this.origem = origem;
    }

    @Override 
    public int read() throws NoSuchElementException, IllegalStateException, IOException, FileNotFoundException {
        try {
            if(origem instanceof FileInputStream) {
                int i = 0;
                String s = ""; 
                while ((i = origem.read()) != -1) {
                    s = s + (char) i;
                }
                origem.close();
                String[] p = s.split(System.lineSeparator());
                for(int c = 0; c < p.length; c += 3) {
                    cand.add(new Candidato(p[c], Integer.parseInt(p[c + 1]), p[c + 2]));
                }

                return 1;
            } else {
                System.out.println("Quantos candidatos?");
                Scanner scan = new Scanner(origem);
                int c = scan.nextInt();

                for(int i = 0; i < c; i++) {
                    String nome = scan.next();
                    Integer numero = scan.nextInt();
                    String partido = scan.next();

                    cand.add(new Candidato(nome, numero, partido));
                }

                scan.close();
                return 1;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public ArrayList<Candidato> getCandidato() {
        return cand;
    }
}