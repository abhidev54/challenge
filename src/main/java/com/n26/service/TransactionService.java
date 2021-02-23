package com.n26.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import com.n26.AppConstants;
import com.n26.dto.Statistics;
import com.n26.dto.Transaction;
import com.n26.exceptions.TransactionTimedoutException;
import com.n26.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository TransactionRepository) {
        super();
        this.transactionRepository = TransactionRepository;
    }

    public void create(Transaction transaction) {
        long timestamp = Instant.parse(transaction.getTimestamp()).atOffset(ZoneOffset.UTC).toEpochSecond();
        long current = Instant.now(Clock.systemUTC()).minusSeconds(AppConstants.TRANSACTION_EXPIRED_OFFSET).getEpochSecond();
        long diff = timestamp - current;

        if (diff < 0) {
            StringBuilder msg = new StringBuilder("Transaction is older than "+AppConstants.TRANSACTION_EXPIRED_OFFSET +" seconds ")
                    .append(transaction.getTimestamp());
            throw new TransactionTimedoutException(msg.toString());
        }

        transactionRepository.createTransaction(transaction);
    }

    public Statistics get() {
       return transactionRepository.getStatistics();
    }

    public void delete() {
        transactionRepository.deleteAllTransactions();
    }
}

