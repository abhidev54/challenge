package com.n26.validation.impl;
import java.math.BigDecimal;
import java.time.*;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.n26.AppConstants;
import com.n26.dto.Transaction;
import com.n26.validation.Fields;
import org.springframework.stereotype.Component;

@Component
public class FieldsValidator implements ConstraintValidator<Fields, Transaction> {


    @Override
    public boolean isValid(Transaction transaction, ConstraintValidatorContext constraintValidatorContext) {
        try {
            new BigDecimal(transaction.getAmount());
        } catch (Exception e) {
            return false;
        }

        try {
            OffsetDateTime.parse(transaction.getTimestamp(), AppConstants.TIMESTAMP_FORMATTER);
        } catch (Exception e) {
            return false;
        }

        // Future date
        long timestamp = Instant.parse(transaction.getTimestamp()).atOffset(ZoneOffset.UTC).toEpochSecond();
        long current = Instant.now(Clock.systemUTC()).plusSeconds(AppConstants.TRANSACTION_EXPIRED_OFFSET).getEpochSecond();

        if (current <= timestamp) {
            return false;
        }

      return true;
    }


}