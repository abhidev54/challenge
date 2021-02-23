package com.n26.repositories;

import com.n26.dto.Statistics;
import com.n26.dto.Transaction;

public interface TransactionRepository {
    void createTransaction(Transaction transaction);
    Statistics getStatistics();
    void deleteAllTransactions();
}
