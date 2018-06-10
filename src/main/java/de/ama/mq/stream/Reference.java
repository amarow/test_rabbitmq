package de.ama.mq.stream;

public class Reference extends Streamable {
    private int objectId;


    public Reference(int objectId) {
        this.objectId = objectId;
    }

    public int getObjectId() {
        return objectId;
    }

}
