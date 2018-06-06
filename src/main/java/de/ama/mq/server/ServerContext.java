package de.ama.mq.server;

import com.rabbitmq.client.*;
import de.ama.mq.RemoteObject;
import de.ama.mq.stream.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ServerContext {
    private Map<Integer, RemoteObject> remoteObjectMap = new HashMap<>();
    private int idGenerator = 1;

    private Connection connection;
    private Channel channel;
    private RpcServer rpcServer;


    public void start() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri("amqp://ama:modrow@localhost");
            factory.setConnectionTimeout(300000);
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare("rpc_queue", false, false, false, null);
            rpcServer = new RpcServer(channel, "rpc_queue") {
                @Override
                public byte[] handleCall(byte[] requestBody, AMQP.BasicProperties replyProperties) {
                    return executeCall(Streamable.fromBytes(requestBody)).toBytes();
                }
            };


            System.out.println("**************************************");
            System.out.println("* ServerContext successfully started *");
            System.out.println("**************************************");

            rpcServer.mainloop();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Streamable executeCall(Streamable data) {
        try {
            if (data instanceof CreateCall) {
                CreateCall call = (CreateCall) data;
                int id = createRemoteObject(call.getServiceClassName());
                return new CreateResult(id);
            } else if (data instanceof MethodCall) {
                MethodCall call = (MethodCall) data;
                RemoteObject remoteObject = getRemoteObject(call.getServiceId());
                Method method = remoteObject.getClass().getMethod(call.getServiceMethodName(), call.getParameterTypes());
                Object result = method.invoke(remoteObject, call.getParameters());
                if (result instanceof RemoteObject){
                    return new CreateResult(registerRemoteObject((RemoteObject) result));
                } else {
                    return new MethodResult(result);
                }
            } else if (data instanceof ReleaseCall) {
                ReleaseCall call = (ReleaseCall) data;
                releaseRemoteObect(call.getServiceId());
                return new Streamable();
            } else {
                return new ErrorResult("unhandled call");
            }
        } catch (Exception e) {
            return new ErrorResult(exceptionAsString(e));
        }
    }

    private String exceptionAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }


    private int createRemoteObject(String serviceClassName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        RemoteObject remoteObject = (RemoteObject) Class.forName(serviceClassName).getDeclaredConstructor().newInstance();
        return registerRemoteObject(remoteObject);
    }

    private int registerRemoteObject(RemoteObject remoteObject) {
        int id = ++idGenerator;
        synchronized (remoteObjectMap){
            remoteObjectMap.put(id, remoteObject);
        }
        System.out.println("registered remote object: "+remoteObject.getClass().getSimpleName() + " id="+id);
        return id;
    }

    private RemoteObject getRemoteObject(int id) {
        synchronized (remoteObjectMap) {
            RemoteObject remoteObject = remoteObjectMap.get(id);
            return remoteObject;
        }
    }

    private void releaseRemoteObect(int id) {
        RemoteObject remoteObject;
        synchronized (remoteObjectMap){
            remoteObject = remoteObjectMap.remove(id);
        }
        System.out.println("released remote object: "+remoteObject.getClass().getSimpleName() + " id="+id);
    }


}
