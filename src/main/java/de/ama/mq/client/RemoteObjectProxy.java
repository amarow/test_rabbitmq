package de.ama.mq.client;

import de.ama.mq.stream.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RemoteObjectProxy implements InvocationHandler,RemoteObjectProxyIfc {
    private int objectId;

    RemoteObjectProxy(int objectId) {
        this.objectId = objectId;
    }

    public int getObjectId() {
        return objectId;
    }

    public Object invoke(Object object, Method method, Object[] args) {

        if (method.getName().equals("getObjectId")){
            return objectId;
        }

        prepareArguments(args);

        MethodParams call = new MethodParams(objectId,method.getName(),args, method.getParameterTypes());
        Streamable mqData = ClientContext.get().callServer(call);

        if (mqData instanceof Reference) {
            Reference result = (Reference) mqData;
            Class returnType = method.getReturnType();
            return ClientContext.get().getOrCreateProxy(returnType,result);
        }

        MethodResult methodResult = (MethodResult) mqData;
        return methodResult.getResult();

    }

    private void prepareArguments(Object[] args) {
        if (args==null) return;
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof RemoteObjectProxyIfc) {
                RemoteObjectProxyIfc remoteObjectProxy = (RemoteObjectProxyIfc) arg;
                Reference reference = new Reference(remoteObjectProxy.getObjectId());
                args[i] = reference;
            }
        }
    }

}
