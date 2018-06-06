package de.ama.mq.client;

import de.ama.mq.stream.CreateResult;
import de.ama.mq.stream.MethodCall;
import de.ama.mq.stream.MethodResult;
import de.ama.mq.stream.Streamable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RemoteObjectProxy implements InvocationHandler,RemoteObjectProxyIfc {
    private int id;

    RemoteObjectProxy(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Object invoke(Object proxy, Method method, Object[] args) {

        if (method.getName().equals("getId")){
            return id;
        }


        MethodCall call = new MethodCall(id,method.getName(),args, method.getParameterTypes());
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
