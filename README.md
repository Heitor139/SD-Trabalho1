# Trabalho 1 — Comunicação entre processos (QXD0043 Sistemas Distribuídos)

Serviço remoto escolhido: **sistema de votação**. Eleitores fazem login por TCP,
recebem a lista de candidatos e votam; notas informativas são difundidas para
todos os clientes por multicast UDP.

Este repositório cobre as **questões 1 a 4** do enunciado.

## Como executar

```bash
./scripts/compilar.sh          # compila tudo em bin/
./scripts/testes.sh            # roda os 7 testes e grava logs + prints em docs/testes/
```

Demonstração interativa (um terminal por processo):

```bash
./scripts/servidor.sh                       # servidor: TCP 5000 + multicast 230.0.0.1:6000
./scripts/eleitor.sh localhost 1111 123
./scripts/eleitor.sh localhost 2222 123
```

Credenciais de teste: eleitores `1111`, `2222`, `3333` e `4444` (senha `123`).

> **Multicast entre máquinas:** por padrão o grupo usa a interface de *loopback*
> (todos os processos na mesma máquina). Para rodar em máquinas diferentes,
> informe a interface física: `java -Dvotacao.interface=wlp2s0 -cp bin ...`

## Organização do código

```
src/votacao/
├── modelo/     POJOs: Candidato, Eleitor, Voto, Mensagem
├── servico/    ServicoCandidatos (cadastro) e ServicoVotacao (login e votos)
├── stream/     CandidatoOutputStream (questão 1) e CandidatoInputStream (questão 2)
├── serial/     Requisicao, Resposta, Marshaller (questão 3)
├── rede/       Protocolo, ServidorVotacao, TratadorCliente, ClienteVotacao,
│               NotificadorMulticast, OuvinteMulticast, InterfaceRede,
│               ServidorStreamCandidatos
├── app/        ServidorApp, AppEleitor, DadosIniciais
└── testes/     Teste1..Teste7
```

| Camada | Responsabilidade |
|---|---|
| `modelo` | dados puros (POJOs `Serializable`), sem lógica de rede |
| `servico` | operações do serviço remoto, com sincronização para o servidor multithread |
| `stream` | representação dos POJOs em sequências de bytes |
| `serial` | empacotamento/desempacotamento das mensagens de request e reply |
| `rede` | sockets TCP (unicast) e UDP (multicast) |
| `app` | processos executáveis (servidor e eleitor) |

## Protocolo

A representação externa de dados usa a **serialização padrão do Java**
(interface `java.io.Serializable`). `Requisicao`, `Resposta` e todos os POJOs
implementam `Serializable`; o `Marshaller` converte o objeto em `byte[]` com
`ObjectOutputStream` e o reconstrói com `ObjectInputStream`.

No socket TCP cada pacote é precedido do seu tamanho em 4 bytes, para que o
receptor saiba onde uma mensagem termina e a próxima começa:

```
[ int tamanho ][ bytes do objeto serializado ]
```

Request: `Requisicao{LOGIN, argumentos={titulo=1111, senha=123}}`

Reply: `Resposta{OK, bem-vindo, Eleitor Um, dados={nome=..., candidatos=[...]}}`

Operações: `LOGIN`, `LISTAR_CANDIDATOS`, `VOTAR`, `ENVIAR_NOTA`, `SAIR`.

Notas multicast: o POJO `Mensagem` (tipo, texto e timestamp) serializado e
enviado em um datagrama UDP, com os tipos `NOTIFICACAO`, `ALERTA` e
`ATUALIZACAO`.

Registro do stream de candidatos (questões 1 e 2): `numero;nome;partido\n`.

## Atendimento ao enunciado

| Item | Onde | Teste |
|---|---|---|
| 1. `CandidatoOutputStream` (destino, array, quantidade) | `stream/CandidatoOutputStream.java` | — |
| 1b. destino `System.out` | | `Teste1SaidaPadrao` |
| 1c. destino `FileOutputStream` | | `Teste2Arquivo` |
| 1d. destino servidor TCP | `rede/ServidorStreamCandidatos.java` | `Teste3Tcp` |
| 2. `CandidatoInputStream` | `stream/CandidatoInputStream.java` | — |
| 2b. origem `System.in` | | `Teste4EntradaPadrao` |
| 2c. origem `FileInputStream` | | `Teste5ArquivoLeitura` |
| 2d. origem cliente TCP | | `Teste3Tcp` |
| 3. serialização (`Serializable`) com empacotamento nos dois lados | `serial/Marshaller.java`, `rede/TratadorCliente.java`, `rede/ClienteVotacao.java` | `Teste6Serializacao` |
| 4a. grupo classe D, join/leave | `rede/OuvinteMulticast.java` | `Teste7Multicast` |
| 4b. NOTIFICACAO / ALERTA / ATUALIZACAO | `rede/NotificadorMulticast.java` | `Teste7Multicast` |
| 4c. múltiplos clientes em tempo real | | `Teste7Multicast` |
| 4d. thread de escuta + thread de interação; servidor concorrente | `app/AppEleitor.java`, `rede/ServidorVotacao.java` | `Teste7Multicast` |
| 4e. TCP para autenticação, UDP para mensagens | `rede/TratadorCliente.java` | `Teste6Serializacao`, `Teste7Multicast` |

## Testes

Todos executados por `./scripts/testes.sh`; as saídas ficam em
[`docs/testes/`](docs/testes/) como `.log` e como capturas de tela `.svg`.
O resumo de cada execução está em [`docs/TESTES.md`](docs/TESTES.md).
