package com.practicaSV.gameLabz.domain;

public class AMQPResponse {

    private String message;

    private ResponseStatus status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public enum ResponseStatus {
        OK,
        ERROR
    }
}
