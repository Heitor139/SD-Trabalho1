# Executar o sistema

Requisitos: JDK 17 ou superior e PowerShell. Execute os comandos na raiz do repositório. Para entender os arquivos Java e o fluxo, consulte o [guia do pacote votacao](src/votacao/README.md).

```powershell
New-Item -ItemType Directory -Force servidor/build-trabalho4 | Out-Null
javac --release 17 -encoding UTF-8 -d servidor/build-trabalho4 (Get-ChildItem servidor/src -Recurse -Filter *.java | ForEach-Object FullName)
```

## Servidor e intermediário

Na máquina A, abra dois terminais e deixe os processos rodando. Use o IPv4 da máquina A no lugar de `192.168.1.20`. Em uma demonstração com todos os processos na mesma máquina, use `127.0.0.1`.

Terminal 1, intermediário na porta 1099:

```powershell
java -cp servidor/build-trabalho4 votacao.console.ServidorIntermediario 192.168.1.20 1099 servidor/build-trabalho4/estado-demo
```

O primeiro argumento é o endereço anunciado aos clientes. O último é o diretório de estado local. Use o mesmo diretório ao reiniciar para preservar assinaturas e pendências.

Terminal 2, servidor de votação na porta 1100:

```powershell
java -cp servidor/build-trabalho4 votacao.console.ServidorVotacao 192.168.1.20 1100 192.168.1.20 1099
```

Os dois primeiros argumentos são o endereço anunciado e a porta do servidor de votação. Os dois seguintes apontam para o intermediário.

## Clientes

Na máquina B, compile o projeto e execute o comando abaixo em cada terminal de cliente. As máquinas precisam estar na mesma rede, com acesso às portas TCP 1099 e 1100 da máquina A.

```powershell
java -cp servidor/build-trabalho4 votacao.console.ConsoleNotificacoes 192.168.1.20 1100 1099
```

A sessão fica aberta e recebe comandos pelo console. Digite `ajuda` para listar os comandos e `sair` para encerrar. O publicador e o assinante usam o mesmo programa.

## Demonstração

1. No primeiro cliente, digite `voto-confirmado ana turma`. Aguarde o ID da assinatura e use `acompanhar <id-da-assinatura>`.
2. No segundo cliente, repita o procedimento com `voto-confirmado bia diretoria`.
3. Em um cliente publicador, digite `publicar VOTACAO turma ALERTA Aviso para a turma.` e `publicar VOTACAO diretoria ALERTA Aviso para a diretoria.`. Ana e Bia devem receber apenas o evento de seu tópico.
4. Encerre o cliente de Ana com `sair`. Publique outro aviso para `turma` e aguarde o recibo. Reinicie o intermediário com o mesmo diretório e reabra o cliente de Ana. Use `consultar <id-da-assinatura>` com o ID original para recuperar o aviso.

A linha `Enfileirado:` mostra o protocolo. O recibo aparece depois. O comando `resultado <protocolo>` recupera o recibo de uma operação específica.

## Teste de integração

Compile primeiro e execute com Python 3:

```powershell
python -X utf8 testes/teste_integracao.py
```

O teste inicia processos separados usando RMI em loopback. Verifica duas eleições, um grupo com nome igual ao de uma eleição, entrega a dois assinantes, retenção para um assinante desligado, recuperação após reinício do intermediário e cancelamento.
