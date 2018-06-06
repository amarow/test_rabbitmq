package de.ama.mq.stream;

public class CreateParams extends Streamable {
    private String className;

    public CreateParams(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
