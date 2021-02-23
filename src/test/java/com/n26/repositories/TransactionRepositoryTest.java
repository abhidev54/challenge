package com.n26.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.dto.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

public class TransactionRepositoryTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionRepository transactionRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateTransaction() throws Exception {
        Transaction transaction = createTransaction("50.00", Instant.now().toString());
        transactionRepository.createTransaction(transaction);
    }

    @Test
    public void testDeleteTransaction() throws Exception {
        transactionRepository.deleteAllTransactions();
    }

    private Transaction createTransaction(String amount, String timestamp) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTimestamp(timestamp);

        return transaction;
    }
}
