package votacao.console;

import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;
import votacao.modelo.Alerta;
import votacao.modelo.Evento;
import votacao.modelo.Mensagem;
import votacao.modelo.Notificacao;
import votacao.modelo.Topico;
import votacao.servico.Comunicacao;

public final class ConsoleNotificacoes {
    private static final String USO = """
            Uso: ConsoleNotificacoes <diretório de estado> <comando>
              voto-confirmado <pessoa> <votação>
              assinar <pessoa> <grupo de notificação>
              publicar <VOTACAO|NOTIFICACAO> <tópico> <ALERTA|NOTIFICACAO|ATUALIZACAO> <texto>
              consultar <id da assinatura>
              cancelar <id da assinatura>
            """;

    private ConsoleNotificacoes() {
    }

    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                throw new IllegalArgumentException(USO);
            }
            Function<Comunicacao, String> operacao = interpretar(args);
            var estado = new EstadoNotificacoes(Path.of(args[0]));
            String resultado = estado.executar(operacao);
            if (!resultado.isEmpty()) {
                System.out.println(resultado);
            }
        } catch (IllegalArgumentException | NoSuchElementException | IOException erro) {
            System.err.println(erro.getMessage());
            System.exit(1);
        }
    }

    private static Function<Comunicacao, String> interpretar(String[] args) {
        return switch (args[1]) {
            case "voto-confirmado" -> {
                validarQuantidade(args, 4);
                yield broker -> broker.registrarVotoConfirmado(args[2], args[3]).getId();
            }
            case "assinar" -> {
                validarQuantidade(args, 4);
                var topico = new Topico(Topico.Tipo.NOTIFICACAO, args[3]);
                yield broker -> broker.assinar(args[2], topico).getId();
            }
            case "publicar" -> {
                validarQuantidade(args, 6);
                var topico = new Topico(Topico.Tipo.valueOf(args[2]), args[3]);
                var evento = new Evento(topico, criarMensagem(args[4], args[5]));
                yield broker -> {
                    broker.publicar(evento);
                    return "Publicado em " + topico.tipo() + "/" + topico.id();
                };
            }
            case "consultar" -> {
                validarQuantidade(args, 3);
                yield broker -> broker.consultarEventos(args[2]).stream()
                        .map(ConsoleNotificacoes::formatarEvento)
                        .collect(Collectors.joining(System.lineSeparator()));
            }
            case "cancelar" -> {
                validarQuantidade(args, 3);
                yield broker -> {
                    broker.cancelarAssinatura(args[2]);
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
