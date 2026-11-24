# Diagrama de classes do Trabalho 4

O diagrama mostra as classes que participam do fluxo de comunicação. `Votacao`, `Candidato` e `Eleitor` continuam no código como entidades do domínio. A confirmação recebe IDs e o fluxo de avisos usa esses IDs sem instanciar essas entidades.

```mermaid
classDiagram
    direction TB
    ConsoleNotificacoes ..> SessaoCliente : inicia sessão interativa
    SessaoCliente ..> CaixaMensagens : acompanha recibos e avisos
    ConsoleNotificacoes ..> CaixaMensagens : enfileira e consulta recibos
    ServidorVotacao ..> CaixaMensagens : encaminha confirmação
    ServidorIntermediario ..> CaixaMensagens : lê entradas e grava recibos
    EstadoNotificacoes ..> ComandosNotificacoes : interpreta comando
    ServidorIntermediario ..> EstadoNotificacoes : executa e distribui
    ComandosNotificacoes ..> Comunicacao
    EstadoNotificacoes ..> Intermediario : carrega e salva
    Intermediario ..|> Comunicacao
    Intermediario "1" *-- "0..*" Assinatura : mantém
    Intermediario "1" --> "0..*" Evento : publica antes de distribuir
    Assinatura "0..*" --> "1" Topico : acompanha
    Assinatura "0..*" --> "0..*" Evento : guarda pendências
    Evento "0..*" --> "1" Topico : destino
    Evento --> "1" Mensagem : conteúdo
    Topico --> Tipo : rótulo
    Mensagem <|-- Notificacao
    Mensagem <|-- Alerta
    class Comunicacao {
        <<interface>>
        +registrarVotoConfirmado(clienteId, votacaoId) Assinatura
        +assinar(clienteId, topico) Assinatura
        +cancelarAssinatura(assinaturaId)
        +publicar(evento)
        +consultarEventos(assinaturaId) List~Evento~
    }
    class Intermediario {
        -assinaturas
        -assinaturasPorTopico
        -publicacoesPendentes
        +distribuirPendencias()
    }
    class EstadoNotificacoes {
        +executar(id, comando) String
        +ultimoId() String
    }
    class Topico {
        +Tipo tipo
        +String id
    }
    class Tipo {
        <<enumeration>>
        VOTACAO
        NOTIFICACAO
    }
```
