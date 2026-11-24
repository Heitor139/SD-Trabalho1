# Notificações de votação

Sistema de avisos para eleições e grupos de notificação, desenvolvido para o Trabalho 4 de Sistemas Distribuídos. A confirmação de voto cria uma assinatura para os avisos da eleição. Grupos de notificação aceitam assinaturas voluntárias.

A aplicação usa **publicar–assinar por rótulos**, com um intermediário central. Cada rótulo combina tipo e identificador. Por exemplo, `VOTACAO/turma` e `NOTIFICACAO/turma` têm assinaturas distintas.

## Arquitetura

| Processo | Responsabilidade |
|---|---|
| Servidor de votação | Recebe confirmações de voto e as encaminha ao intermediário. |
| Intermediário | Mantém assinaturas, distribui publicações por rótulo e guarda os avisos pendentes. |
| Cliente | Executa uma sessão de console para publicar, inscrever-se e acompanhar avisos. Várias instâncias podem permanecer abertas. |

O sistema mantém papéis de cliente e servidor. A confirmação passa pelo servidor de votação, e os clientes usam o intermediário para publicar e receber avisos. Os processos Java trocam mensagens por caixas em um diretório compartilhado, que todos precisam conseguir acessar. O estado de assinaturas e avisos pertence ao intermediário.

## Execução e testes

O [guia de execução](servidor/README.md) apresenta os requisitos, a compilação, a demonstração e os testes. O [diagrama de classes](servidor/diagrama-classes.md) mostra as dependências entre os componentes.

## Relatório técnico

O **desacoplamento espacial** vem do endereçamento por rótulo. O publicador precisa informar o tópico e o conteúdo do aviso. O intermediário encontra as assinaturas correspondentes. O **desacoplamento temporal** vem da retenção. Uma publicação pode ser enfileirada com o intermediário ou o assinante desligado. O assinante pode voltar e consultar a mesma assinatura.

O cliente obtém um protocolo ao enfileirar o comando e exibe o recibo quando ele fica disponível. No modo de acompanhamento, o cliente consulta os avisos periodicamente. O recebimento depende da leitura da caixa pelo cliente. A publicação segue sem aguardar a leitura do aviso.

O intermediário recupera seu estado ao reiniciar com o mesmo diretório. O resultado da última operação fica junto ao estado para permitir a recuperação do recibo caso o processo caia entre gravar o estado e retirar o comando da entrada. Os recibos permanecem em disco. Uma consulta consome o lote de avisos, que continua acessível pelo protocolo do recibo.

Para N assinantes do tópico, o intermediário insere o evento em N caixas. O índice por rótulo restringe esse trabalho ao tópico de destino. O estado completo é lido e gravado a cada operação, e a consulta periódica adiciona latência. O log do intermediário mede o processamento da publicação. Para observar o custo até a entrega, meça também o tempo até a exibição no cliente, mantendo o texto constante e variando o número de assinaturas. Agrupar gravações pode reduzir o custo de disco. Aumentar o intervalo de consulta reduz acessos e aumenta o tempo para exibir avisos.

A aplicação usa confirmações de voto como entrada para o serviço de notificações. O escopo da demonstração abrange assinaturas, publicação e entrega de avisos.
