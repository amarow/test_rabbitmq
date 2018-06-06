package de.ama.mq.stream;

public class ReleaseCall extends Streamable {
    private int         serviceId;

    public ReleaseCall(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getServiceId() {
        return serviceId;
    }

}
