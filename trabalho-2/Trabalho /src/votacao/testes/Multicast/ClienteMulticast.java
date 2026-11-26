package votacao.testes.Multicast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import votacao.rede.OuvinteMulticast;
import votacao.rede.Protocolo;
import votacao.serial.Marshaller;
import votacao.serial.Requisicao;
import votacao.serial.Resposta;

public class ClienteMulticast {

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        String titulo = args.length > 1 ? args[1] : "1111";
        String senha = args.length > 2 ? args[2] : "123";

        if (!autenticar(host, titulo, senha)) {
            return;
        }

        OuvinteMulticast ouvinte = new OuvinteMulticast(
                mensagem -> System.out.println("[AVISO] " + mensagem));

        try (BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in))) {
            ouvinte.entrarNoGrupo();

            ouvinte.start();

            System.out.println("Autenticado. Digite sair para desconectar.");

            while (!"sair".equalsIgnoreCase(teclado.readLine())) {
                System.out.println("Digite sair para desconectar.");
            }
        } finally {
            ouvinte.sairDoGrupo();
        }
    }

    private static boolean autenticar(String host, String titulo, String senha)
            throws Exception {
        try (Socket socket = new Socket(host, Protocolo.PORTA_AUTENTICACAO)) {

            Requisicao login = new Requisicao(Protocolo.LOGIN)
                    .com("titulo", titulo)
                    .com("senha", senha);

            Marshaller.enviar(socket.getOutputStream(), login);

            Resposta resposta = (Resposta) Marshaller.receber(socket.getInputStream());

            System.out.println("[TCP] " + resposta.getStatus() + " - "
                    + resposta.getMensagem());
            
            return resposta.sucesso();

        }
    }
}
