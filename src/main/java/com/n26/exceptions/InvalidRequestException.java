package com.n26.exceptions;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(final String msg) {
        super(msg);
    }
}
