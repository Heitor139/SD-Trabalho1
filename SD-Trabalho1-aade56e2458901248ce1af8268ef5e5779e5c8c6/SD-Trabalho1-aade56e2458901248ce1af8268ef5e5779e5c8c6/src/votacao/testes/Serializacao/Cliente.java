package votacao.testes.Serializacao;

import java.net.Socket;
import votacao.app.DadosIniciais;
import votacao.modelo.Candidato;
import votacao.rede.Protocolo;
import votacao.serial.Marshaller;
import votacao.serial.Resposta;

/** Demonstra o cliente da questao 3 com uma unica mensagem serializada. */
public class Cliente {

    public static void main(String[] args) throws Exception {

        String host = args.length > 0 ? args[0] : "localhost";
        Candidato[] candidatos = DadosIniciais.candidatos();

        try (Socket socket = new Socket(host, Protocolo.PORTA_TCP)) {

            System.out.println("[cliente] enviando objeto: " + java.util.Arrays.toString(candidatos));

            Marshaller.enviar(socket.getOutputStream(), candidatos);

            Resposta resposta = (Resposta) Marshaller.receber(socket.getInputStream());

            System.out.println("[cliente] resposta desempacotada: "
                    + resposta.getStatus() + " - " + resposta.getMensagem());
        }
    }
}
