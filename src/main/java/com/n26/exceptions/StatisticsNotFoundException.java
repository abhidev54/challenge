package com.n26.exceptions;

public class StatisticsNotFoundException extends RuntimeException {
    public StatisticsNotFoundException(final String msg) {
        super(msg);
    }
}
