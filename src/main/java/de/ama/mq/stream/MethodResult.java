package de.ama.mq.stream;

public class MethodResult extends Streamable {
    private Object      result;


    public MethodResult(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return result;
    }

}
