export class ClienteApi {
    constructor(urlBase) {
        this.urlBase = urlBase;
    }

    async cadastrarVotacao(votacao) {
        throw new Error("Ainda nao implementado");
    }

    async listarVotacoes() {
        throw new Error("Ainda nao implementado");
    }

    async assinar(clienteId, votacaoId) {
        throw new Error("Ainda nao implementado");
    }

    async cancelarAssinatura(assinaturaId) {
        throw new Error("Ainda nao implementado");
    }

    async publicar(votacaoId, mensagem) {
        throw new Error("Ainda nao implementado");
    }

    async consultarEventos(assinaturaId) {
        throw new Error("Ainda nao implementado");
    }
}
