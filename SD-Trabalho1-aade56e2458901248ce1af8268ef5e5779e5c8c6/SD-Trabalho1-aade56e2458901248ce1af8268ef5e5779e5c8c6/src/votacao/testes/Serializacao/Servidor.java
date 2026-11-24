package votacao.testes.Serializacao;

import java.net.ServerSocket;
import java.net.Socket;
import votacao.modelo.Candidato;
import votacao.rede.Protocolo;
import votacao.serial.Marshaller;
import votacao.serial.Resposta;

public class Servidor {

    public static void main(String[] args) throws Exception {
        try (ServerSocket servidor = new ServerSocket(Protocolo.PORTA_TCP)) {

            System.out.println("[servidor] aguardando um cliente na porta "
                    + Protocolo.PORTA_TCP + "...");

            try (Socket cliente = servidor.accept()) {

                Candidato[] candidatos = (Candidato[]) Marshaller.receber(cliente.getInputStream());
                
                System.out.println("[servidor] objeto desempacotado: " + java.util.Arrays.toString(candidatos));
                
                Marshaller.enviar(cliente.getOutputStream(),
                        Resposta.ok("Candidatos recebidos com sucesso"));
            }
        }
        System.out.println("[servidor] encerrado.");
    }
}
