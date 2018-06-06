package de.ama.mq.stream;

public class CreateResult extends Streamable {
    private int objectId;


    public CreateResult(int objectId) {
        this.objectId = objectId;
    }

    public int getObjectId() {
        return objectId;
    }

}
