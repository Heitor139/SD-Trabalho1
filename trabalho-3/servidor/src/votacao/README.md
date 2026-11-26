# Fluxo e implementação

Consulte o [guia de execução](../../README.md) para iniciar os processos. O [diagrama de classes](../../diagrama-classes.md) apresenta a estrutura do código.

## Da confirmação ao aviso

1. `voto-confirmado ana turma` chama o servidor de votação por RMI. O servidor encaminha o comando ao intermediário, que cria ou reutiliza uma assinatura para `VOTACAO/turma`. O fluxo usa o nome da votação como identificador e aceita um nome novo sem cadastro prévio. A confirmação é simulada.
2. `publicar VOTACAO turma ALERTA A votação termina às 17h.` envia ao intermediário o rótulo, a categoria e o texto. O intermediário grava o comando em uma fila local e devolve o protocolo.
3. O processo do intermediário lê a fila, com uma pausa de 250 ms entre rodadas. A regra de publicação localiza as assinaturas ativas daquele rótulo e acrescenta um evento a cada lista de pendências. Uma publicação sem assinaturas é processada sem entregas.
4. `acompanhar <id-da-assinatura>` inicia consultas periódicas pelo cliente, com pausa de dois segundos entre verificações. O intermediário retira os avisos pendentes e grava o lote no recibo. O cliente lê o recibo por RMI e exibe as mensagens. A sessão também verifica recibos de comandos manuais em segundo plano.

Os rótulos `VOTACAO/turma` e `NOTIFICACAO/turma` são independentes. Um `ALERTA` pode ser publicado para qualquer um deles, pois a categoria do texto é independente do tipo do tópico. O servidor de votação encaminha confirmações conforme chegam. As publicações surgem de comandos do publicador.

## Comandos do cliente

| Comando | Efeito |
|---|---|
| `voto-confirmado ana turma` | Inscreve Ana em `VOTACAO/turma` por meio do servidor de votação. |
| `assinar ana calendario` | Inscreve Ana em `NOTIFICACAO/calendario`. |
| `publicar VOTACAO turma ALERTA Restam cinco minutos.` | Enfileira um evento. A categoria aceita `ALERTA`, `NOTIFICACAO` ou `ATUALIZACAO`. |
| `consultar <id-da-assinatura>` | Retira o lote de avisos pendentes e gera um recibo com o conteúdo. |
| `resultado <protocolo>` | Lê o recibo de um comando. Enquanto indisponível, exibe `Pendente.`. |
| `acompanhar <id-da-assinatura>` | Ativa consultas automáticas para uma assinatura. |
| `parar-acompanhamento` | Para de iniciar novas consultas automáticas. A inscrição permanece ativa. |
| `cancelar <id-da-assinatura>` | Remove a assinatura e descarta os avisos pendentes dela. |
| `ajuda` | Lista os comandos. |
| `sair` | Encerra a sessão. O intermediário mantém as assinaturas. |

O protocolo identifica uma operação. O ID devolvido por `assinar` ou `voto-confirmado` identifica a assinatura. Os clientes podem ter várias assinaturas. O acompanhamento automático da sessão seleciona uma de cada vez.

## Arquivos Java

| Arquivo | Responsabilidade |
|---|---|
| [ConsoleNotificacoes.java](console/ConsoleNotificacoes.java) | Inicia uma sessão ou executa um comando avulso. |
| [SessaoCliente.java](console/SessaoCliente.java) | Lê o teclado e verifica recibos e avisos em segundo plano. |
| [ClienteRede.java](console/ClienteRede.java) | Envia confirmações ao servidor de votação, demais comandos ao intermediário e lê recibos do intermediário. |
| [RedeNotificacoes.java](console/RedeNotificacoes.java) | Contrato RMI de envio e consulta de recibos. |
| [RmiNotificacoes.java](console/RmiNotificacoes.java) | Publica e localiza serviços no registro RMI usando endereço e porta. |
| [ServidorVotacao.java](console/ServidorVotacao.java) | Exporta o serviço que recebe e encaminha confirmações de voto. |
| [ServidorIntermediario.java](console/ServidorIntermediario.java) | Exporta o serviço de entrada do intermediário. Processa a fila local em sequência, grava recibos e mede o processamento. |
| [CaixaMensagens.java](console/CaixaMensagens.java) | Grava comandos e recibos no disco do intermediário. Os clientes acessam o serviço pela rede. |
| [EstadoNotificacoes.java](console/EstadoNotificacoes.java) | Recupera o estado, aplica comandos e salva a última operação para recuperação. |
| [ComandosNotificacoes.java](console/ComandosNotificacoes.java) | Valida argumentos, cria tópicos e eventos e traduz comandos para operações de serviço. |
| [Comunicacao.java](servico/Comunicacao.java) | Contrato local de assinatura, publicação e consulta. |
| [Intermediario.java](servico/Intermediario.java) | Mantém os índices de assinaturas, aplica cancelamentos e roteia publicações por tópico. |
| [Topico.java](modelo/Topico.java) | Identifica o destino pelo par tipo e nome. |
| [Assinatura.java](modelo/Assinatura.java) | Liga o cliente a um tópico e guarda seus eventos pendentes. |
| [Evento.java](modelo/Evento.java) | Associa uma mensagem ao tópico de destino. |
| [Mensagem.java](modelo/Mensagem.java) | Guarda categoria, texto e data. Representa diretamente as atualizações. |
| [Alerta.java](modelo/Alerta.java) | Especializa a mensagem como alerta. |
| [Notificacao.java](modelo/Notificacao.java) | Especializa a mensagem como notificação. |
| [Votacao.java](modelo/Votacao.java) | Modela uma votação com nome, candidatos e eleitores. O fluxo de avisos usa o ID do tópico. |
| [Eleitor.java](modelo/Eleitor.java) | Modela o eleitor. A entrada de confirmação usa um identificador textual. |
| [Candidato.java](modelo/Candidato.java) | Modela nome, número e partido. As assinaturas estão associadas à votação. |
