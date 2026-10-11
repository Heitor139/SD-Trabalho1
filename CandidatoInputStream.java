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
    private Candidato cand = null;

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
                cand = new Candidato(p[0], Integer.parseInt(p[1]), p[2]);

                return 1;
            } else {
                Scanner scan = new Scanner(origem);

                String nome = scan.nextLine();
                Integer numero = scan.nextInt();
                String partido = scan.nextLine();

                cand = new Candidato(nome, numero, partido);

                scan.close();
                return 1;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public Candidato getCandidato() {
        return cand;
    }
}