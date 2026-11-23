# Console de notificações

A aplicação Java distribui comunicados por tópico. O pacote `servico` contém o intermediário, e o pacote `modelo` contém os tópicos, as inscrições e os eventos. Os comandos em `console` operam sobre o estado compartilhado em arquivo.

## Compilação

Execute na raiz do repositório com JDK 17 ou superior e Bash.

```bash
mkdir -p servidor/build
find servidor/src -name '*.java' -print0 |
  xargs -0 javac --release 17 -encoding UTF-8 -d servidor/build
```

## Demonstração em dois terminais

Nos dois terminais, a partir da raiz do repositório, defina a função apontando para o mesmo diretório de estado.

```bash
notificacoes() {
  java -cp servidor/build votacao.console.ConsoleNotificacoes servidor/build/estado-demo "$@"
}
```

No **terminal do assinante**, registre a confirmação do voto de Ana em `turma` e inscreva-a no grupo de notificação `calendario`. Os comandos devolvem os IDs das inscrições e terminam.

```bash
ID_VOTO=$(notificacoes voto-confirmado ana turma)
ID_AVISOS=$(notificacoes assinar ana calendario)
```

No **terminal do publicador**, envie três comunicados. O publicador usa o tópico e os dados da mensagem.

```bash
notificacoes publicar VOTACAO turma ALERTA 'A votação encerra às 17h.'
notificacoes publicar VOTACAO diretorio ATUALIZACAO 'Apuração iniciada.'
notificacoes publicar NOTIFICACAO calendario NOTIFICACAO 'O calendário foi atualizado.'
```

No **terminal do assinante**, consulte as duas inscrições. Cada consulta inicia um novo processo. Ana recebe o alerta de `turma` e o aviso de `calendario`. Uma nova consulta da primeira inscrição sai sem imprimir mensagens, pois o lote já foi consumido.

```bash
notificacoes consultar "$ID_VOTO"
notificacoes consultar "$ID_AVISOS"
notificacoes consultar "$ID_VOTO"
```

Use `notificacoes cancelar "$ID_AVISOS"` para encerrar a inscrição e descartar suas pendências. Repetir a inscrição da mesma pessoa no mesmo tópico devolve o mesmo ID enquanto a inscrição estiver ativa. Uma inscrição começa a acumular comunicados a partir da sua criação.

Os terminais precisam acessar o mesmo diretório em um sistema de arquivos que suporte bloqueio de arquivo e substituição atômica. Para repetir a demonstração com estado vazio, use outro diretório nos dois terminais.
