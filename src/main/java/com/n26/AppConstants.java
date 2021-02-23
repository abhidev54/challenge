package com.n26;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class AppConstants {
    public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneId.of("UTC"));

    public static final int TRANSACTION_EXPIRED_OFFSET = 60;
    public static final int SCALE = 2;

    public static final String PATH_TRANSACTION = "/transactions";
    public static final String PATH_STATISTICS = "/statistics";
}
