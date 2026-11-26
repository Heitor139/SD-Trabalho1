package votacao.console;

import java.rmi.RemoteException;

final class ClienteRede {
    private final String host;
    private final int portaVotos;
    private final int portaIntermediario;

    ClienteRede(String host, int portaVotos, int portaIntermediario) {
        this.host = host;
        this.portaVotos = portaVotos;
        this.portaIntermediario = portaIntermediario;
    }

    String enviar(String[] comando) throws RemoteException {
        ComandosNotificacoes.interpretar(comando);
        int porta = comando[0].equals("voto-confirmado") ? portaVotos : portaIntermediario;
        return RmiNotificacoes.conectar(host, porta).enviar(comando);
    }

    String resultado(String protocolo) throws RemoteException {
        return RmiNotificacoes.conectar(host, portaIntermediario).resultado(protocolo);
    }
}
