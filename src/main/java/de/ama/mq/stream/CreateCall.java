package de.ama.mq.stream;

public class CreateCall extends Streamable {
    private String      serviceClassName;

    public CreateCall(String serviceClassName) {
        this.serviceClassName = serviceClassName;
    }

    public String getServiceClassName() {
        return serviceClassName;
    }
}
