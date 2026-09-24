package votacao.app;

import votacao.modelo.Mensagem;
import votacao.rede.NotificadorMulticast;
import votacao.rede.Protocolo;
import votacao.rede.ServidorVotacao;
import votacao.servico.ServicoCandidatos;
import votacao.servico.ServicoVotacao;

/**
 * Processo servidor: TCP multithreading (questao 3) para login, lista de
 * candidatos e votos; multicast UDP (questao 4) para as notas informativas.
 *
 * Uso: java votacao.app.ServidorApp
 */
public class ServidorApp {

    public static void main(String[] args) throws Exception {
        ServicoCandidatos servicoCandidatos = DadosIniciais.servicoCandidatos();
        ServicoVotacao servicoVotacao = new ServicoVotacao(servicoCandidatos);
        DadosIniciais.cadastrarEleitores(servicoVotacao);

        try (ServidorVotacao servidor =
                     new ServidorVotacao(Protocolo.PORTA_TCP, servicoCandidatos, servicoVotacao)) {
            servidor.iniciar();

            try (NotificadorMulticast notificador = new NotificadorMulticast()) {
                notificador.enviar(Mensagem.NOTIFICACAO,
                        "Servidor no ar com " + servicoCandidatos.quantidade() + " candidatos");
            }

            System.out.println("[servidor] pronto; Ctrl+C para encerrar");
            Thread.currentThread().join();
        }
    }
}
