package org.alexdev.duckhttpd.exceptions;

public class NoServerResponseException extends Exception {
    public NoServerResponseException() {
        super();
    }
    public NoServerResponseException(String message) {
        super(message);
    }
}
