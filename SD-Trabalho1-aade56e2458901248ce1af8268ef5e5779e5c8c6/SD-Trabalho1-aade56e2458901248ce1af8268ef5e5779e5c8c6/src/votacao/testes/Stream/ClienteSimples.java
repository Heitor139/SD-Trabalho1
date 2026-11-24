package votacao.testes.Stream;

import java.net.Socket;
import votacao.app.DadosIniciais;
import votacao.modelo.Candidato;
import votacao.stream.CandidatoOutputStream;

public class ClienteSimples {
    public static void main(String[] args) throws Exception {
        int porta = 9000;
        Candidato[] candidatos = DadosIniciais.candidatos();
        
        System.out.println("Conectando ao servidor...");

        try (Socket socket = new Socket("localhost", porta);
             CandidatoOutputStream saida = new CandidatoOutputStream(
                     socket.getOutputStream(), candidatos, candidatos.length)) {
            
            System.out.println("Conectado! Enviando candidatos...");
            saida.enviarCandidatos();
            
            System.out.println("Candidatos enviados com sucesso!");
        }
    }
}