# Cliente Python

Projeto cliente independente do servidor Java. Requer Python 3.

- cliente_api.py: operações da API HTTP/JSON.
- main.py: entrada do console.

O transporte previsto utiliza urllib.request e json, da biblioteca padrão. O cliente poderá publicar, assinar e consultar eventos de uma ou mais eleições, sem hospedar um servidor de callbacks.

Os métodos ainda não estão implementados. A API do servidor também precisa ser implementada antes de executar o cliente.
