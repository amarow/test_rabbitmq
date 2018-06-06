package de.ama.mq.stream;

public class ErrorResult extends Streamable {
    private String      errorMsg;


    public ErrorResult(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
