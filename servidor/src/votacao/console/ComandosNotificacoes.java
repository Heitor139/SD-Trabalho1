package votacao.console;

import java.util.function.Function;
import java.util.stream.Collectors;
import votacao.modelo.Alerta;
import votacao.modelo.Evento;
import votacao.modelo.Mensagem;
import votacao.modelo.Notificacao;
import votacao.modelo.Topico;
import votacao.servico.Comunicacao;

final class ComandosNotificacoes {
    static final String USO = """
            Uso: ConsoleNotificacoes <diretório de mensagens> [comando]
              voto-confirmado <pessoa> <votação>
              assinar <pessoa> <grupo de notificação>
              publicar <VOTACAO|NOTIFICACAO> <tópico> <ALERTA|NOTIFICACAO|ATUALIZACAO> <texto>
              consultar <id da assinatura>
              resultado <id do comando>
              acompanhar <id da assinatura>
              cancelar <id da assinatura>
            """;

    private ComandosNotificacoes() {
    }

    static Function<Comunicacao, String> interpretar(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException(USO);
        }
        return switch (args[0]) {
            case "voto-confirmado" -> {
                validarQuantidade(args, 3);
                yield broker -> broker.registrarVotoConfirmado(args[1], args[2]).getId();
            }
            case "assinar" -> {
                validarQuantidade(args, 3);
                var topico = new Topico(Topico.Tipo.NOTIFICACAO, args[2]);
                yield broker -> broker.assinar(args[1], topico).getId();
            }
            case "publicar" -> {
                validarQuantidade(args, 5);
                var topico = new Topico(Topico.Tipo.valueOf(args[1]), args[2]);
                var evento = new Evento(topico, criarMensagem(args[3], args[4]));
                yield broker -> {
                    broker.publicar(evento);
                    return "Publicado em " + topico.tipo() + "/" + topico.id();
                };
            }
            case "consultar" -> {
                validarQuantidade(args, 2);
                yield broker -> broker.consultarEventos(args[1]).stream()
                        .map(ComandosNotificacoes::formatarEvento)
                        .collect(Collectors.joining(System.lineSeparator()));
            }
            case "cancelar" -> {
                validarQuantidade(args, 2);
                yield broker -> {
                    broker.cancelarAssinatura(args[1]);
                    return "Inscrição cancelada.";
                };
            }
            default -> throw new IllegalArgumentException(USO);
        };
    }

    private static void validarQuantidade(String[] args, int quantidade) {
        if (args.length != quantidade) {
            throw new IllegalArgumentException(USO);
        }
    }

    private static Mensagem criarMensagem(String tipo, String texto) {
        if (texto.isBlank()) {
            throw new IllegalArgumentException("Informe o texto do comunicado.");
        }
        return switch (tipo) {
            case Mensagem.ALERTA -> new Alerta(texto);
            case Mensagem.NOTIFICACAO -> new Notificacao(texto);
            case Mensagem.ATUALIZACAO -> new Mensagem(Mensagem.ATUALIZACAO, texto);
            default -> throw new IllegalArgumentException("Tipo de mensagem desconhecido: " + tipo);
        };
    }

    private static String formatarEvento(Evento evento) {
        Topico topico = evento.topico();
        return "[" + topico.tipo() + "/" + topico.id() + "] " + evento.mensagem();
    }
}
