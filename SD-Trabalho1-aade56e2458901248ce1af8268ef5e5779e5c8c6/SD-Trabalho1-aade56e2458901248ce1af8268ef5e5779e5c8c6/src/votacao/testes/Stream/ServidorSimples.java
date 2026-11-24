package votacao.testes.Stream;

import java.net.ServerSocket;
import java.net.Socket;
import votacao.modelo.Candidato;
import votacao.stream.CandidatoInputStream;

public class ServidorSimples {
    public static void main(String[] args) throws Exception {
        int porta = 9000;
        System.out.println("Servidor aguardando conexão na porta " + porta + "...");

        try (ServerSocket servidor = new ServerSocket(porta);
             Socket cliente = servidor.accept(); 
             CandidatoInputStream entrada = new CandidatoInputStream(cliente.getInputStream())) {
            
            System.out.println("Cliente conectou! Lendo candidatos...");
            
            Candidato candidato;
            while ((candidato = entrada.lerCandidato()) != null) {
                System.out.println("Recebido: " + candidato);
            }
            
            System.out.println("Fim da leitura. Servidor encerrado.");
        }
    }
}