package votacao.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import votacao.modelo.Candidato;
import votacao.modelo.Mensagem;
import votacao.rede.ClienteVotacao;
import votacao.rede.OuvinteMulticast;
import votacao.rede.Protocolo;
import votacao.serial.Resposta;

/**
 * Cliente do eleitor (questao 4d): uma thread escuta o multicast e a thread
 * principal cuida da interacao com o usuario.
 *
 * Uso: java votacao.app.AppEleitor [host] [titulo] [senha]
 */
public class AppEleitor {

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

        String titulo = args.length > 1 ? args[1] : perguntar(teclado, "Titulo: ");
        String senha = args.length > 2 ? args[2] : perguntar(teclado, "Senha: ");

        OuvinteMulticast ouvinte = new OuvinteMulticast(
                m -> System.out.println("\n>>> " + m + "\n> "));
        ouvinte.entrarNoGrupo();
        ouvinte.start();

        try (ClienteVotacao cliente = new ClienteVotacao(host, Protocolo.PORTA_TCP)) {
            Resposta login = cliente.login(titulo, senha);
            System.out.println("[login] " + login.getStatus() + ": " + login.getMensagem());
            if (!login.sucesso()) {
                return;
            }
            mostrarCandidatos(ClienteVotacao.candidatosDe(login));
            menu(teclado, cliente);
        } finally {
            ouvinte.sairDoGrupo();
            System.out.println("[multicast] saiu do grupo");
        }
    }

    private static void menu(BufferedReader teclado, ClienteVotacao cliente) throws Exception {
        while (true) {
            System.out.print("\n[1] listar  [2] votar  [3] enviar nota multicast  [0] sair\n> ");
            String opcao = teclado.readLine();
            if (opcao == null || "0".equals(opcao.trim())) {
                return;
            }
            switch (opcao.trim()) {
                case "1":
                    mostrarCandidatos(ClienteVotacao.candidatosDe(cliente.listarCandidatos()));
                    break;
                case "2":
                    imprimir(cliente.votar(perguntar(teclado, "Numero do candidato: ")));
                    break;
                case "3":
                    String tipo = perguntar(teclado, "Tipo (NOTIFICACAO/ALERTA/ATUALIZACAO): ");
                    imprimir(cliente.enviarNota(tipo.isEmpty() ? Mensagem.NOTIFICACAO : tipo,
                            perguntar(teclado, "Mensagem: ")));
                    break;
                default:
                    System.out.println("opcao invalida");
            }
        }
    }

    static void mostrarCandidatos(List<Candidato> candidatos) {
        System.out.println("--- candidatos em votacao ---");
        for (Candidato candidato : candidatos) {
            System.out.println("  " + candidato);
        }
    }

    static void imprimir(Resposta resposta) {
        System.out.println("[" + resposta.getStatus() + "] " + resposta.getMensagem());
        resposta.getDados().forEach((chave, valor) -> System.out.println("    " + chave + " = " + valor));
    }

    static String perguntar(BufferedReader teclado, String rotulo) throws java.io.IOException {
        System.out.print(rotulo);
        String linha = teclado.readLine();
        return linha == null ? "" : linha.trim();
    }
}
