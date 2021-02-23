package com.n26.exceptions;

public class TransactionTimedoutException extends RuntimeException {
    public TransactionTimedoutException(final String msg) {
        super(msg);
    }
}
