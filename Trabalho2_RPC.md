# Trabalho 2 - RPC para notificacoes distribuidas

## O que o enunciado pede

O Trabalho 2 solicita reimplementar a questao 4 do Trabalho 1. A questao 4 distribui notificacoes, alertas e atualizacoes para varios clientes. Nesta versao, o servidor mantem os clientes inscritos e encaminha as mensagens por chamadas RPC usando Java RMI. O codigo da aplicacao nao cria sockets diretamente.

As chamadas seguem o protocolo requisicao-resposta da secao 5.2. Cada mensagem serializada contem:

| Campo | Tipo | Uso na implementacao |
|---|---|---|
| `messageType` | `int` (`0=Request`, `1=Reply`) | Distingue requisicao e resposta. |
| `requestId` | `int` | Correlaciona cada resposta com a requisicao correspondente. |
| `objectReference` | `RemoteObjectRef` | Identifica host, porta e nome do objeto remoto. |
| `methodId` | `int` | Seleciona a operacao remota. |
| `arguments` | `byte[]` | Contem os argumentos ou o resultado serializado. |

O cliente executa `doOperation`, empacota os argumentos, envia a mensagem ao endpoint RMI e aguarda a resposta. O endpoint do servidor recebe a requisicao, decodifica os argumentos, seleciona o metodo e devolve uma mensagem `Reply`. No sentido inverso, cada cliente exporta um endpoint de callback e o servidor invoca nele a operacao de recebimento de mensagem.

## Servico implementado

O servico escolhido e a distribuicao de comunicados do sistema de votacao. O administrador informa uma notificacao, alerta ou atualizacao no console do servidor, e o servidor encaminha a mensagem aos clientes inscritos. Esta entrega implementa a comunicacao de avisos da questao 4; nao implementa a abertura de votacao nem a contabilizacao de votos.

O servidor publica o endpoint `VotacaoRPC`. Cada cliente cria o endpoint `ClienteRPC` para receber callbacks e registra no servidor seu identificador e uma `RemoteObjectRef` para esse endpoint. O servidor guarda as referencias em `GrupoClientes`. Quando o administrador informa o tipo e o texto, o processo servidor invoca `ENVIAR_NOTIFICACAO`; o servico percorre uma copia dos membros registrados e executa `RECEBER_MENSAGEM` em cada callback. O cliente imprime o tipo e o texto recebidos. Se um callback falhar, o cliente correspondente e removido do grupo.

O cliente aceita `listar` para consultar os identificadores registrados e `sair` para cancelar a inscricao e encerrar o processo. A distribuicao e feita sequencialmente pelo servidor: cada chamada de callback termina antes de o servidor enviar a mensagem ao proximo cliente.

As classes `Notificacao` e `Alerta` especializam `Mensagem`; atualizacoes usam diretamente `Mensagem`. `Votacao` agrega colecoes de candidatos e eleitores para atender aos requisitos de modelagem do trabalho, mas essas entidades nao participam do servico de comunicados nesta implementacao.

## Operacoes remotas

| ID | Operacao | Descricao |
|---:|---|---|
| 1 | `ENTRAR_GRUPO` | Registra o identificador e a referencia remota do cliente. |
| 2 | `SAIR_GRUPO` | Remove o cliente do grupo. |
| 3 | `ENVIAR_NOTIFICACAO` | Distribui uma mensagem aos clientes registrados. |
| 4 | `LISTAR_CLIENTES` | Retorna os identificadores registrados. |
| 5 | `RECEBER_MENSAGEM` | Callback remoto que exibe a mensagem no cliente. |

## Requisitos de modelagem

- Entidades: `Candidato`, `Eleitor`, `Mensagem` e `Votacao`.
- Agregacoes: `Votacao` mantem colecoes de candidatos e eleitores; `ServidorNotificacoes` mantem um `GrupoClientes`.
- Extensoes: `Notificacao` e `Alerta` estendem `Mensagem`.
- Referencias remotas: `RemoteObjectRef` identifica o endpoint RPC do servidor ou o callback do cliente. As chamadas acessam os objetos remotos pela referencia.
- Passagem por valor: argumentos e resultados sao codificados em arrays de bytes por `RpcCodec`, usando serializacao Java.

## Execucao

Compile a partir da raiz do repositorio:

```powershell
New-Item -ItemType Directory -Force bin-java
javac -encoding UTF-8 -d bin-java (Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName)
```

Inicie o servidor em um terminal:

```powershell
java -cp bin-java votacao.rpc.ServidorRPC
```

Inicie cada cliente em outro terminal:

```powershell
java -cp bin-java votacao.rpc.ClienteRPC
```

No cliente, digite `listar` para ver os participantes registrados ou `sair` para encerrar a inscricao. No servidor, informe um tipo (`notificacao`, `alerta` ou `atualizacao`) e, na linha seguinte, o texto a distribuir. Para maquinas diferentes, informe o endereco do servidor ao cliente e o endereco anunciado pelo cliente:

```powershell
java -cp bin-java votacao.rpc.ServidorRPC 1099 192.168.1.10
java -cp bin-java votacao.rpc.ClienteRPC 192.168.1.10 1099 192.168.1.20 1100 eleitor-1
```

O endereco anunciado por cada processo precisa estar acessivel pelos demais participantes. O RMI usa portas de registro e portas de objetos remotos, que devem estar acessiveis entre os hosts. A aplicacao nao instancia `Socket`, `ServerSocket` nem `MulticastSocket`.
