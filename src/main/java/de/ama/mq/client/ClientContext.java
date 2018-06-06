package de.ama.mq.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.RpcClient;
import de.ama.mq.Implementation;
import de.ama.mq.RemoteObject;
import de.ama.mq.stream.*;
import de.ama.mq.stream.ErrorResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Der Clientcontext verwaltet alle clientseitigen {@link RemoteObject}e. Hierüber können {@link RemoteObject}e erzeugt
 * und wieder freigegeben werden.
 */
public class ClientContext {
    private int    TIMEOUT = 30000;
    private Object monitor = new Object();
    private Connection connection;
    private Channel channel;
    private RpcClient rpcClient;
    private List<RemoteObjectProxyIfc> remoteObjectProxies = new ArrayList<>();

    private static ClientContext singleton;

    public static void start(String user, String pwd, String host) {
        if (singleton != null) {
            throw new RuntimeException("ClientContext allready started");
        }
        singleton = new ClientContext(user, pwd, host);
    }

    public static ClientContext get() {
        return singleton;
    }


    private ClientContext(String user, String pwd, String host) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri("amqp://" + user + ":" + pwd + "@" + host);
            factory.setConnectionTimeout(TIMEOUT);
            connection = factory.newConnection();
            channel = connection.createChannel();
            rpcClient = new RpcClient(channel, "", "rpc_queue", TIMEOUT);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public <T extends RemoteObject> T createRemoteObject(Class<T> ifc) {
        try {

            String implementationClassName = ifc.getName().replace("Ifc","");
            if (ifc.isAnnotationPresent(Implementation.class)){
                implementationClassName = ifc.getAnnotation(Implementation.class).name();
            }

            CreateParams call = new CreateParams(implementationClassName);
            Streamable mqData = callServer(call);

            RemoteObjectProxyIfc objectProxy = createRemoteObjectProxy(ifc, (CreateResult) mqData);
            return (T) objectProxy;

        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    <T extends RemoteObject> RemoteObjectProxyIfc createRemoteObjectProxy(Class<T> ifc, CreateResult result) {
        RemoteObjectProxyIfc objectProxy = (RemoteObjectProxyIfc) java.lang.reflect.Proxy.newProxyInstance(ifc.getClassLoader(), new Class[]{ifc,RemoteObjectProxyIfc.class}, new RemoteObjectProxy(result.getObjectId()));
        synchronized (monitor){
            remoteObjectProxies.add(objectProxy);
        }
        return objectProxy;
    }


    public void releaseRemoteObject(RemoteObject remoteObject) {
        RemoteObjectProxyIfc proxy = (RemoteObjectProxyIfc) remoteObject;
        ReleaseParams mqMessage = new ReleaseParams(proxy.getObjectId());
        callServer(mqMessage);
        removeProxy(remoteObject);
    }

    public void close() {
        for (RemoteObjectProxyIfc proxy : remoteObjectProxies) {
            releaseRemoteObject(proxy);
        }
    }

    Streamable callServer(Streamable data) {
        try {
            byte[] bytes = rpcClient.primitiveCall(data.toBytes());
            Streamable mqData = Streamable.fromBytes(bytes);
            handleErrorResponse(mqData);
            return mqData;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleErrorResponse(Streamable mqData) {
        if (mqData instanceof ErrorResult) {
            ErrorResult errorResponse = (ErrorResult) mqData;
            throw new RuntimeException("Error from ServerContext: " + errorResponse.getErrorMsg());
        }
    }

    private void removeProxy(RemoteObject toRemove) {
        ArrayList<RemoteObjectProxyIfc> temp = new ArrayList<>();
        for (RemoteObjectProxyIfc proxy : remoteObjectProxies) {
            if (toRemove!=proxy){
                temp.add(proxy);
            }
        }
        synchronized (monitor){
            remoteObjectProxies =temp;
        }
    }
}
