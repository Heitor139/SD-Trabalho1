# Trabalho 4 — Notificações de votação

O sistema distribui avisos, alertas e atualizações sobre votações e grupos de notificação. Ao receber a confirmação de um voto, o intermediário inscreve a pessoa automaticamente no tópico daquela votação. A pessoa também pode se inscrever em grupos de notificação, como `calendario`.

## Publicar e assinar

O projeto adota a opção B do Trabalho 4 de Sistemas Distribuídos da UFC. Cada tópico tem um tipo e um identificador. `VOTACAO/turma` e `NOTIFICACAO/turma` são destinos distintos, mesmo com o mesmo nome. O publicador informa o tópico, e o intermediário guarda o comunicado para cada inscrição ativa naquele tópico.

O **desacoplamento espacial** vem do endereçamento por tópico. O publicador e os assinantes compartilham o diretório de estado do intermediário. O publicador direciona o comunicado ao tópico e o intermediário determina as inscrições de destino.

O **desacoplamento temporal** vem da retenção das pendências. O assinante pode encerrar seu processo e consultar os comunicados mais tarde usando o mesmo ID de inscrição. A publicação funciona quando os assinantes estão desligados, e cada inscrição tem sua própria lista de pendências.

## Custos e recuperação

O índice por tópico limita a distribuição às inscrições do destino. A demonstração mantém o estado em arquivo, o que permite usar processos separados e recuperar inscrições e pendências entre execuções. Cada comando lê e grava o estado completo, e as operações que usam o mesmo diretório aguardam a vez para atualizar o arquivo. Em um cenário de maior volume, armazenar as pendências separadamente e gravar apenas as alterações reduziria a contenção e o custo de disco.

O estado é substituído de forma atômica. A consulta consome as pendências, de modo que encerrar o processo após gravar a consulta, mas antes de exibir a resposta, pode fazer o assinante perder aquele lote. Uma confirmação de recebimento permitiria adiar a remoção até o assinante processar os eventos.

## Execução

O console requer JDK 17 ou superior. Veja o [roteiro da demonstração](servidor/README.md) e o [diagrama de classes](servidor/diagrama-classes.svg).
