# Trabalho 3 - Sistema de Comunicados de Votação

Esqueleto de uma API HTTP/JSON com servidor Java e clientes Python e JavaScript, em projetos separados.

```text
servidor/              API Java e intermediário próprio
clientes/python/       Cliente Python
clientes/javascript/   Cliente JavaScript
contratos/             Contrato HTTP/JSON compartilhado
```

O administrador publica um comunicado para uma eleição. O intermediário associa o evento às assinaturas daquela eleição. Cada cliente consulta seus eventos pela API. A assinatura independe do candidato escolhido.

O publicar-assinar é a organização escolhida para o serviço de comunicados. O intermediário próprio executará dentro do processo servidor da API.

## Módulos

- [Servidor](servidor/README.md)
- [Cliente Python](clientes/python/README.md)
- [Cliente JavaScript](clientes/javascript/README.md)
- [Contrato da API](contratos/api.md)
