package com.n26.service;

import com.n26.AppConstants;
import com.n26.dto.Statistics;
import com.n26.dto.Transaction;
import com.n26.repositories.TransactionRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import java.time.Instant;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

   @InjectMocks
    private TransactionService transactionService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateTransaction() {
        transactionService.create(new Transaction("10.00", Instant.now().toString()));
    }

    @Test
    public void testDeleteTransaction() {
        transactionService.delete();
        Statistics statistics = transactionService.get();
        Assert.assertNull("All statistics deleted ",statistics);
    }

    @Test
    public void testGetStatistics() {
        Statistics inputStatistic = Statistics.newBuilder().withCount(3).withSum("40.44").withAvg("4.6").withMin("3.5")
                .withMax("10.40").build();
        Statistics statistics = transactionService.get();
    }

    @Test
    public void testCreationTransactionWithOldDate() {
        String timestamp = Instant.now().minusSeconds(AppConstants.TRANSACTION_EXPIRED_OFFSET + 120).toString();
        try {
            transactionService.create(new Transaction("10.00", timestamp));
        } catch (Exception ex) {
            final String expected = "Transaction is older than "+AppConstants.TRANSACTION_EXPIRED_OFFSET +" seconds "+timestamp;
            Assert.assertEquals(expected, ex.getMessage());
        }
    }

}
