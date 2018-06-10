package de.ama.mq.server;

import com.rabbitmq.client.*;
import de.ama.mq.RemoteObject;
import de.ama.mq.stream.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Der {@link ServerContext} verwaltet alle serverseitigen {@link RemoteObject}e. Die {@link RemoteObject}e sind über
 * eine objectId mit einem clientseitigen Proxy verbunden. Clientseitige Aufrufe auf dem Proxy werden an das serverseitige
 * {@link RemoteObject} weitergegeben.
 */
public class ServerContext {
    private static ServerContext singleton;
    private Map<Integer, RemoteObject> remoteObjectMap = new HashMap<>();

    private Connection connection;
    private Channel channel;
    private RpcServer rpcServer;
    private boolean verbose;

    public static ServerContext get() {
        return singleton;
    }

    public void start(String queueName) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri("amqp://ama:modrow@localhost");
            factory.setConnectionTimeout(300000);
            connection = factory.newConnection();
            connection.clearBlockedListeners();
            channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
            rpcServer = new RpcServer(channel, queueName) {
                @Override
                public byte[] handleCall(byte[] requestBody, AMQP.BasicProperties replyProperties) {
                    return executeCall(Streamable.fromBytes(requestBody)).toBytes();
                }
            };


            Thread thread = new Thread("rabbitmq mainloop") {
                @Override
                public void run() {
                    try {
                        rpcServer.mainloop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();

            if (verbose){
                System.out.println("**************************************");
                System.out.println("* ServerContext successfully started *");
                System.out.println("**************************************");
            }
            singleton = this;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Streamable executeCall(Streamable data) {
        try {
            if (data instanceof CreateParams) {
                CreateParams call = (CreateParams) data;
                int id = createRemoteObject(call.getClassName());
                return new Reference(id);
            } else if (data instanceof MethodParams) {
                MethodParams methodParams = (MethodParams) data;
                Object[] parameters = methodParams.getParameters();
                prepareParameters(parameters);
                RemoteObject remoteObject = getRemoteObject(methodParams.getObjectId());
                Method method = remoteObject.getClass().getMethod(methodParams.getMethodName(), methodParams.getParameterTypes());
                Object result = method.invoke(remoteObject, parameters);
                if (result instanceof RemoteObject){
                    return new Reference(registerRemoteObject((RemoteObject) result));
                } else {
                    return new MethodResult(result);
                }
            } else if (data instanceof Reference) {
                Reference call = (Reference) data;
                releaseRemoteObect(call.getObjectId());
                return new Streamable();
            } else {
                return new ErrorResult("unhandled call");
            }
        } catch (Exception e) {
            return new ErrorResult(exceptionAsString(e));
        }
    }

    private void prepareParameters(Object[] parameters) {
        if (parameters==null) return;
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            if (parameter instanceof Reference) {
                Reference reference = (Reference) parameter;
                RemoteObject remoteObject = getRemoteObject(reference.getObjectId());
                parameters[i]=remoteObject;
            }
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

    private RemoteObject getRemoteObject(int id) {
        synchronized (remoteObjectMap) {
            RemoteObject remoteObject = remoteObjectMap.get(id);
            return remoteObject;
        }
    }

    private int registerRemoteObject(RemoteObject remoteObject) {
        int id = remoteObject.hashCode();
        synchronized (remoteObjectMap){
            if (remoteObjectMap.put(id, remoteObject)==null && verbose){
                System.out.println("registered remote object: "+remoteObject.getClass().getSimpleName() + " id="+id);
            }
        }
        return id;
    }

    private void releaseRemoteObect(int id) {
        synchronized (remoteObjectMap){
            RemoteObject removed = remoteObjectMap.remove(id);
            if (removed !=null && verbose){
                System.out.println("released remote object: "+removed.getClass().getSimpleName() + " id="+id);
            }
        }
    }


    public int getSize() {
        return remoteObjectMap.size();
    }

    public static void main(String[] args) {
        new ServerContext().start("mandant1");
    }
}
