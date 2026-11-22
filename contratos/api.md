# Contrato previsto da API

Base de exemplo: http://localhost:8080/api

Corpos de requisição e resposta utilizarão application/json. Este documento define o contrato planejado, os endpoints ainda não estão implementados.

| Objeto | Método | Caminho | Operação |
|---|---|---|---|
| VotacoesApi | POST | /votacoes | Cadastrar eleição |
| VotacoesApi | GET | /votacoes | Listar eleições |
| AssinaturasApi | POST | /assinaturas | Assinar comunicados de uma eleição |
| AssinaturasApi | DELETE | /assinaturas/{id} | Cancelar assinatura |
| ComunicadosApi | POST | /comunicados | Publicar evento no tópico de uma eleição |
| ComunicadosApi | GET | /comunicados?assinaturaId={id} | Consultar eventos pendentes da assinatura |

## Cadastrar eleição

Requisição:

```json
{"id":"turma","nome":"Representante de turma","candidatos":[],"eleitores":[]}
```

Resposta prevista: 201 Created, contendo a eleição e a referência /api/votacoes/turma. O cabeçalho Location apontará para essa referência.

## Assinar

Requisição:

```json
{"clienteId":"pessoa-a","votacaoId":"turma"}
```

Resposta prevista: 201 Created.

```json
{"id":"assinatura-1","clienteId":"pessoa-a","votacaoId":"turma","referencia":"/api/assinaturas/assinatura-1"}
```

A mesma pessoa poderá possuir assinaturas em eleições distintas. A referência da assinatura será utilizada para operações posteriores.

## Publicar comunicado

Requisição:

```json
{"votacaoId":"turma","mensagem":{"tipo":"ALERTA","mensagem":"Votação encerra em cinco minutos."}}
```

Resposta prevista: 202 Accepted. O intermediário identificará o tópico e associará o evento a cada assinatura correspondente. Essa resposta não confirma leitura pelos clientes.

## Consultar eventos

Resposta prevista: 200 OK.

```json
[{"topico":{"votacaoId":"turma"},"mensagem":{"tipo":"ALERTA","mensagem":"Votação encerra em cinco minutos.","timestamp":1790000000000}}]
```

A primeira implementação poderá manter eventos em memória e removê-los após a consulta. Essa política não garante recuperação se a resposta se perder. Confirmação de recebimento, persistência, autenticação e reconexão ainda não estão definidas ou implementadas.

## Demais respostas

Listar eleições: 200 OK com um array de eleições. Cancelar assinatura: 204 No Content. Recurso inexistente: 404. JSON inválido: 400. Operação ainda não implementada: 501, quando houver tratamento HTTP de erros.

Objetos serão enviados por valor em JSON. URIs identificarão os recursos remotos; não haverá transferência de referências de memória Java nem serialização Java no protocolo HTTP.
