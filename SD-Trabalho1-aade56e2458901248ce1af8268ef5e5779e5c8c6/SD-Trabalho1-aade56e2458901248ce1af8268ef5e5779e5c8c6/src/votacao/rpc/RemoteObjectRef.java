package votacao.rpc;

import java.io.Serializable;

public final class RemoteObjectRef implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String host;
    private final int port;
    private final String objectName;

    public RemoteObjectRef(String host, int port, String objectName) {
        this.host = host;
        this.port = port;
        this.objectName = objectName;
    }

    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getObjectName() { return objectName; }
}
