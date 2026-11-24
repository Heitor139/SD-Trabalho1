package votacao.console;

import java.rmi.RemoteException;

public final class ServidorVotacao implements RedeNotificacoes {
    private final String hostIntermediario;
    private final int portaIntermediario;

    private ServidorVotacao(String hostIntermediario, int portaIntermediario) {
        this.hostIntermediario = hostIntermediario;
        this.portaIntermediario = portaIntermediario;
    }

    public static void main(String[] args) throws RemoteException {
        if (args.length != 4) {
            throw new IllegalArgumentException(
                    "Uso: ServidorVotacao <host anunciado> <porta> <host intermediário> <porta intermediário>");
        }
        var servidor = new ServidorVotacao(args[2], Integer.parseInt(args[3]));
        RmiNotificacoes.publicar(args[0], Integer.parseInt(args[1]), servidor);
        System.out.println("Servidor de votação pronto na porta " + args[1] + ".");
    }

    @Override
    public String enviar(String[] comando) throws RemoteException {
        if (comando == null || comando.length != 3 || !"voto-confirmado".equals(comando[0])) {
            throw new IllegalArgumentException("O servidor recebe confirmações de voto.");
        }
        return RmiNotificacoes.conectar(hostIntermediario, portaIntermediario).enviar(comando);
    }

    @Override
    public String resultado(String protocolo) throws RemoteException {
        throw new RemoteException("Consulte o recibo no intermediário.");
    }
}
