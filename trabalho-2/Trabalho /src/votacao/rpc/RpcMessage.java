package votacao.rpc;

import java.io.Serializable;

public final class RpcMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final int REQUEST = 0;
    public static final int REPLY = 1;
    private final int messageType;
    private final int requestId;
    private final RemoteObjectRef objectReference;
    private final int methodId;
    private final byte[] arguments;

    public RpcMessage(int messageType, int requestId, RemoteObjectRef objectReference,
            int methodId, byte[] arguments) {

        this.messageType = messageType;
        this.requestId = requestId;
        this.objectReference = objectReference;
        this.methodId = methodId;
        this.arguments = arguments;
    }

    public int getMessageType() { return messageType; }
    public int getRequestId() { return requestId; }
    public RemoteObjectRef getObjectReference() { return objectReference; }
    public int getMethodId() { return methodId; }
    public byte[] getArguments() { return arguments; }
    
}
