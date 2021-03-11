package com.patres.framework;

public class FrameworkException extends RuntimeException {

    public FrameworkException(final String message) {
        super(message);
    }

    public FrameworkException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
