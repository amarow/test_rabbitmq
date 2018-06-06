package de.ama.mq.stream;

public class MethodParams extends Streamable {
    private int        objectId;
    private String     methodName;
    private Object[]   parameters;
    private Class<?>[] parameterTypes;

    public MethodParams(int objectId, String methodName, Object[] parameters, Class<?>[] parameterTypes) {
        this.objectId = objectId;
        this.methodName = methodName;
        this.parameters = parameters;
        this.parameterTypes = parameterTypes;
    }


    public Object[] getParameters() {
        return parameters;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public int getObjectId() {
        return objectId;
    }

    public String getMethodName() {
        return methodName;
    }

}
