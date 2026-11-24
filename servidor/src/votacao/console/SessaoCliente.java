package votacao.console;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

final class SessaoCliente {
    private final CaixaMensagens caixas;
    private final ConcurrentHashMap<String, String> pendentes = new ConcurrentHashMap<>();
    private final AtomicReference<String> assinaturaAcompanhada = new AtomicReference<>();
    private String assinaturaConsultada;
    private String consultaEmAndamento;

    SessaoCliente(CaixaMensagens caixas) {
        this.caixas = caixas;
    }

    void executar() {
        var tarefas = Executors.newSingleThreadScheduledExecutor();
        tarefas.scheduleWithFixedDelay(this::mostrarRecibos, 0, 250, TimeUnit.MILLISECONDS);
        tarefas.scheduleWithFixedDelay(this::consultarAssinatura, 0, 2, TimeUnit.SECONDS);
        try (var entrada = new Scanner(System.in)) {
            System.out.println("Cliente de notificações. Digite ajuda ou sair.");
            while (entrada.hasNextLine()) {
                String linha = entrada.nextLine().strip();
                if (linha.equals("sair")) {
                    return;
                }
                if (!linha.isEmpty()) {
                    receberComando(linha);
                }
            }
        } finally {
            tarefas.shutdownNow();
        }
    }

    private void receberComando(String linha) {
        String[] argumentos = linha.split("\\s+", 5);
        try {
            switch (argumentos[0]) {
                case "ajuda" -> System.out.println(ComandosNotificacoes.USO + "  parar-acompanhamento\n  sair");
                case "acompanhar" -> {
                    ConsoleNotificacoes.validarId(argumentos);
                    assinaturaAcompanhada.set(argumentos[1]);
                    System.out.println("Acompanhando " + argumentos[1]);
                }
                case "parar-acompanhamento" -> {
                    assinaturaAcompanhada.set(null);
                    System.out.println("Acompanhamento interrompido.");
                }
                case "resultado" -> {
                    ConsoleNotificacoes.validarId(argumentos);
                    String resultado = caixas.resultado(argumentos[1]);
                    System.out.println(resultado == null ? "Pendente." : resultado);
                }
                default -> {
                    String id = ConsoleNotificacoes.enviar(caixas, argumentos);
                    System.out.println("Enfileirado: " + id);
                    pendentes.put(id, argumentos[0]);
                }
            }
        } catch (IllegalArgumentException | IOException erro) {
            System.err.println(erro.getMessage());
        }
    }

    private void mostrarRecibos() {
        try {
            for (var comando : pendentes.entrySet()) {
                String retorno = caixas.resultado(comando.getKey());
                if (retorno != null) {
                    pendentes.remove(comando.getKey());
                    String mensagem = retorno.isEmpty() ? "Sem avisos." : retorno;
                    System.out.println("[" + comando.getValue() + "] " + mensagem);
                }
            }
        } catch (IOException erro) {
            System.err.println("Falha ao ler recibos: " + erro.getMessage());
        }
    }

    private void consultarAssinatura() {
        try {
            if (consultaEmAndamento != null) {
                String retorno = caixas.resultado(consultaEmAndamento);
                if (retorno == null) {
                    return;
                }
                consultaEmAndamento = null;
                if (!retorno.isEmpty()) {
                    System.out.println(retorno);
                }
                if (retorno.startsWith("Erro: ")) {
                    assinaturaAcompanhada.compareAndSet(assinaturaConsultada, null);
                }
            }
            String assinatura = assinaturaAcompanhada.get();
            if (assinatura != null) {
                assinaturaConsultada = assinatura;
                consultaEmAndamento = ConsoleNotificacoes.enviar(caixas, new String[]{"consultar", assinatura});
            }
        } catch (IOException erro) {
            System.err.println("Falha ao acompanhar avisos: " + erro.getMessage());
        }
    }
}
