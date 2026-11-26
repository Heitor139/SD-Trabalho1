package votacao.testes.Multicast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import votacao.modelo.Mensagem;
import votacao.rede.NotificadorMulticast;
import votacao.rede.Protocolo;
import votacao.serial.Marshaller;
import votacao.serial.Requisicao;
import votacao.serial.Resposta;

public class ServidorMulticast {

    public static void main(String[] args) {
        ExecutorService clientes = Executors.newCachedThreadPool();

        try (ServerSocket autenticacao = new ServerSocket(Protocolo.PORTA_AUTENTICACAO);
             NotificadorMulticast notificador = new NotificadorMulticast();
             Scanner scanner = new Scanner(System.in)) {

            Thread aceitar = new Thread(() -> aceitarClientes(autenticacao, clientes),
                    "autenticacao-tcp");
            aceitar.setDaemon(true);
            aceitar.start();

            System.out.println("TCP autenticacao: " + Protocolo.PORTA_AUTENTICACAO);
            System.out.println("UDP multicast: " + Protocolo.GRUPO_MULTICAST
                    + ":" + Protocolo.PORTA_MULTICAST);

            while (true) {
                System.out.print("[Servidor] Escolha um tipo de mensagem (ou sair): ");
                String tipo = scanner.nextLine();
                String mensagem = scanner.nextLine();
                if (tipo.equalsIgnoreCase("sair")) {
                    break;
                }
                if (tipo.equalsIgnoreCase("notificacao")) {
                    notificador.enviar(Mensagem.NOTIFICACAO, mensagem);
                } else if (tipo.equalsIgnoreCase("atualizacao")) {
                    notificador.enviar(Mensagem.ATUALIZACAO, mensagem);
                } else {
                    notificador.enviar(Mensagem.ALERTA, mensagem);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clientes.shutdownNow();
        }
    }

    private static void aceitarClientes(ServerSocket servidor, ExecutorService clientes) {
        while (!servidor.isClosed()) {
            try {
                Socket cliente = servidor.accept();
                clientes.submit(() -> autenticar(cliente));
            } catch (IOException e) {
                if (!servidor.isClosed()) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    private static void autenticar(Socket socket) {
        try (Socket cliente = socket) {

            Object objeto = Marshaller.receber(cliente.getInputStream());

            Requisicao requisicao = (Requisicao) objeto;

            boolean valido = Protocolo.LOGIN.equals(requisicao.getOperacao())
                    && "1111".equals(requisicao.texto("titulo"))
                    && "123".equals(requisicao.texto("senha"));

            Resposta resposta = valido
                    ? Resposta.ok("Autenticado")
                    : Resposta.erro("Credenciais invalidas");
            Marshaller.enviar(cliente.getOutputStream(), resposta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
