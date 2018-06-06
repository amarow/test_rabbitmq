package de.ama.mq.stream;

public class MethodCall extends Streamable {
    private int         serviceId;
    private String      serviceMethodName;
    private Object[]    parameters;
    private Class<?>[]  parameterTypes;

    public MethodCall(int serviceId, String serviceMethodName, Object[] parameters, Class<?>[] parameterTypes) {
        this.serviceId = serviceId;
        this.serviceMethodName = serviceMethodName;
        this.parameters = parameters;
        this.parameterTypes = parameterTypes;
    }


    public Object[] getParameters() {
        return parameters;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getServiceMethodName() {
        return serviceMethodName;
    }

}
