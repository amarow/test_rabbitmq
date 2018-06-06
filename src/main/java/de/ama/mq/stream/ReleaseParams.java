package de.ama.mq.stream;

public class ReleaseParams extends Streamable {
    private int objectId;

    public ReleaseParams(int objectId) {
        this.objectId = objectId;
    }

    public int getObjectId() {
        return objectId;
    }

}
