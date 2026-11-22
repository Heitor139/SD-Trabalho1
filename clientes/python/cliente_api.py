class ClienteApi:
    def __init__(self, url_base):
        self.url_base = url_base

    def cadastrar_votacao(self, votacao):
        raise NotImplementedError

    def listar_votacoes(self):
        raise NotImplementedError

    def assinar(self, cliente_id, votacao_id):
        raise NotImplementedError

    def cancelar_assinatura(self, assinatura_id):
        raise NotImplementedError

    def publicar(self, votacao_id, mensagem):
        raise NotImplementedError

    def consultar_eventos(self, assinatura_id):
        raise NotImplementedError
