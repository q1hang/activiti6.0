package com.q1hang.activiti.common.exception;

public class RRException extends RuntimeException {
    public RRException() {
        super();
    }

    public RRException(String message) {
        super(message);
    }

    public RRException(String message, Throwable cause) {
        super(message, cause);
    }

    public RRException(Throwable cause) {
        super(cause);
    }

    protected RRException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
