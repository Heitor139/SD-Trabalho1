# Guia de execução

O sistema roda com JDK 17 ou superior. Execute os comandos desta página no PowerShell, a partir da raiz do repositório.

Para entender os comandos, o fluxo de mensagens e os arquivos Java, consulte o [guia interno do pacote votacao](src/votacao/README.md).

## Compilar

```powershell
New-Item -ItemType Directory -Force servidor/build-trabalho4 | Out-Null
javac --release 17 -encoding UTF-8 -d servidor/build-trabalho4 (Get-ChildItem servidor/src -Recurse -Filter *.java | ForEach-Object FullName)
```

## Iniciar os processos

Os quatro terminais abaixo são janelas independentes da mesma demonstração. Execute um comando de inicialização por terminal. O servidor de votação, o intermediário e cada cliente permanecem em execução. Use o mesmo diretório de mensagens em todos os comandos. Para começar com estado vazio, escolha um novo caminho.

**Terminal 1 — Intermediário**

```powershell
java -cp servidor/build-trabalho4 votacao.console.ServidorIntermediario servidor/build-trabalho4/demo
```

**Terminal 2 — Servidor de votação**

```powershell
java -cp servidor/build-trabalho4 votacao.console.ServidorVotacao servidor/build-trabalho4/demo
```

**Terminais 3 e 4 — Clientes**

Execute o mesmo comando nos dois terminais. Nesta demonstração, o primeiro cliente acompanha avisos e o segundo publica. Qualquer sessão pode fazer as duas coisas.

```powershell
java -cp servidor/build-trabalho4 votacao.console.ConsoleNotificacoes servidor/build-trabalho4/demo
```

## Publicar e acompanhar avisos

Os próximos comandos são digitados **dentro do cliente**, depois de iniciá-lo. Digite `ajuda` para ver os comandos ou `sair` para encerrar a sessão.

No cliente assinante, confirme um voto na eleição `turma`.

```text
voto-confirmado ana turma
```

A sessão mostra o protocolo de envio e, após o processamento, exibe `[voto-confirmado]` seguido do ID da assinatura. Copie esse ID.

```text
acompanhar <id-da-assinatura>
```

No cliente publicador, envie um alerta para o rótulo `VOTACAO/turma`. Digite o texto da mensagem sem aspas.

```text
publicar VOTACAO turma ALERTA A votação encerra às 17h.
```

O cliente assinante exibe o alerta quando consulta sua caixa. O comando `parar-acompanhamento` suspende as consultas automáticas e mantém a sessão aberta. O comando `consultar <id-da-assinatura>` faz uma consulta avulsa.

## Demonstrar retenção e roteamento

1. No assinante, digite `sair`. No publicador, publique outro alerta em `VOTACAO/turma`.
2. Reinicie somente o cliente assinante e retome o acompanhamento com o mesmo ID. O aviso publicado durante a ausência deve aparecer.
3. Para testar a recuperação do intermediário, encerre-o com Ctrl+C e publique mais um aviso. O publicador recebe um protocolo. Reinicie o intermediário com o mesmo diretório e observe o recibo e a entrega.
4. Para testar rótulos distintos, use `assinar ana turma` para criar uma assinatura no grupo `NOTIFICACAO/turma`. Publique com `publicar NOTIFICACAO turma NOTIFICACAO Aviso do grupo.` e consulte o ID do grupo. O aviso vai para o grupo, separado da assinatura de votação.

O comando `resultado <protocolo>` recupera um recibo anterior, inclusive depois de reabrir o cliente. Também é possível passar um comando após o diretório na linha de inicialização para executar uma operação avulsa em scripts.

## Teste automatizado

Compile o projeto e execute o teste com Python 3. O teste inicia os processos e usa um diretório temporário.

```powershell
python -X utf8 testes/teste_integracao.py
```

| Critério | Evidência |
|---|---|
| Corretude arquitetural (40%) | Entrega para dois assinantes do mesmo rótulo, separação de tipos com o mesmo nome e comandos que endereçam tópicos. |
| Robustez (30%) | Publicação com o intermediário e o destinatário desligados, recuperação após reinício e tratamento de cancelamento e entrada inválida. |
| Relatório (20%) | Comparação do tempo de processamento no log e do tempo até a exibição no cliente, em rodadas com diferentes quantidades de assinaturas. |
| Demonstração (10%) | Execução das sessões e recebimento de um aviso publicado durante a ausência do destinatário. |
