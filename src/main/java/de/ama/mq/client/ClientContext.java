package de.ama.mq.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.RpcClient;
import de.ama.mq.Implementation;
import de.ama.mq.RemoteObject;
import de.ama.mq.stream.CreateParams;
import de.ama.mq.stream.ErrorResult;
import de.ama.mq.stream.Reference;
import de.ama.mq.stream.Streamable;

import java.util.HashMap;
import java.util.Map;

/**
 * Der Clientcontext verwaltet alle clientseitigen {@link RemoteObject}e. Hierüber können {@link RemoteObject}e erzeugt
 * und wieder freigegeben werden.
 */
public class ClientContext {
    private int    TIMEOUT = 30000;
    private Connection connection;
    private Channel channel;
    private RpcClient rpcClient;
    private Map<Integer,RemoteObjectProxyIfc> remoteObjectProxies = new HashMap<>();

    private static ClientContext singleton;

    public static void start(String queuName) {
        if (singleton != null) {
            throw new RuntimeException("ClientContext allready started");
        }
        singleton = new ClientContext(queuName);
    }

    public static ClientContext get() {
        return singleton;
    }


    private ClientContext(String queueName) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri("amqp://ama:modrow@localhost");
            factory.setConnectionTimeout(TIMEOUT);
            connection = factory.newConnection();
            connection.clearBlockedListeners();
            channel = connection.createChannel();
            rpcClient = new RpcClient(channel, "", queueName, TIMEOUT);

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

            RemoteObjectProxyIfc objectProxy = getOrCreateProxy(ifc, (Reference) mqData);
            return (T) objectProxy;

        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    <T extends RemoteObject> RemoteObjectProxyIfc getOrCreateProxy(Class<T> ifc, Reference reference) {
        synchronized (remoteObjectProxies){
            RemoteObjectProxyIfc proxy = remoteObjectProxies.get(reference.getObjectId());
            if (proxy==null){
                proxy = (RemoteObjectProxyIfc) java.lang.reflect.Proxy.newProxyInstance(ifc.getClassLoader(),
                        new Class[]{ifc,RemoteObjectProxyIfc.class},
                        new RemoteObjectProxy(reference.getObjectId()));
                remoteObjectProxies.put(proxy.getObjectId(),proxy);
            }
            return proxy;
        }
    }


    public void releaseRemoteObject(RemoteObject remoteObject) {
        RemoteObjectProxyIfc proxy = (RemoteObjectProxyIfc) remoteObject;
        Reference mqMessage = new Reference(proxy.getObjectId());
        callServer(mqMessage);
        removeProxy(proxy);
    }

    public void close() {
        for (RemoteObjectProxyIfc proxy : remoteObjectProxies.values()) {
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

    private void removeProxy(RemoteObjectProxyIfc toRemove) {
        synchronized (remoteObjectProxies){
            remoteObjectProxies.remove(toRemove.getObjectId());
        }
    }
}
