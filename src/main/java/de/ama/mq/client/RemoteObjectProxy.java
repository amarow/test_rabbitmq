package de.ama.mq.client;

import de.ama.mq.stream.CreateResult;
import de.ama.mq.stream.MethodParams;
import de.ama.mq.stream.MethodResult;
import de.ama.mq.stream.Streamable;

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

    public Object invoke(Object proxy, Method method, Object[] args) {

        if (method.getName().equals("getObjectId")){
            return objectId;
        }


        MethodParams call = new MethodParams(objectId,method.getName(),args, method.getParameterTypes());
        Streamable mqData = ClientContext.get().callServer(call);

        if (mqData instanceof CreateResult) {
            CreateResult result = (CreateResult) mqData;
            Class returnType = method.getReturnType();
            return ClientContext.get().createRemoteObjectProxy(returnType,result);
        }

        MethodResult methodResult = (MethodResult) mqData;
        return methodResult.getResult();

    }

}
