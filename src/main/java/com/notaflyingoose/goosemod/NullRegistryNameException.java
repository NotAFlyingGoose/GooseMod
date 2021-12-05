package com.notaflyingoose.goosemod;

public class NullRegistryNameException extends Exception {
    public NullRegistryNameException() {
    }

    public NullRegistryNameException(String message) {
        super(message);
    }

    public NullRegistryNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullRegistryNameException(Throwable cause) {
        super(cause);
    }

    public NullRegistryNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
