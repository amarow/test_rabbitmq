package de.ama.mq.stream;

public class CreateResult extends Streamable {
    private int id;


    public CreateResult(int serviceId) {
        this.id = serviceId;
    }

    public int getId() {
        return id;
    }

}
