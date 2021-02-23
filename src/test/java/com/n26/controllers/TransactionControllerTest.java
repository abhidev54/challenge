package com.n26.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.AppConstants;
import com.n26.contollers.transaction.TransactionController;
import com.n26.dto.Transaction;
import com.n26.exceptions.TransactionTimedoutException;
import com.n26.service.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.ZoneOffset;

public class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    public void testAddTransaction() throws Exception {
        Transaction transaction = createTransaction("2.00", Instant.now().toString());
        Mockito.doNothing().when(transactionService).create(transaction);

        mockMvc.perform(post(AppConstants.PATH_TRANSACTION).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().is(HttpServletResponse.SC_CREATED));
    }

    @Test
    public void testAddTransactionInvalidFormatAmount() throws Exception {
        Transaction transaction = createTransaction("2.00Test", Instant.now().toString());
        Mockito.doNothing().when(transactionService).create(transaction);

        mockMvc.perform(post(AppConstants.PATH_TRANSACTION).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().is(422));
                //Added hardcoded value (422) because HttpServletResponse doesn't have 422 status code
    }

    @Test
    public void testAddTransactionFutureTransactionDate() throws Exception {
        Transaction transaction = createTransaction("2.00", Instant.now().plusSeconds(AppConstants.TRANSACTION_EXPIRED_OFFSET + 1).toString());
        Mockito.doNothing().when(transactionService).create(transaction);

        mockMvc.perform(post(AppConstants.PATH_TRANSACTION).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().is(422));
    }

    @Test
    public void testAddTransactionOldTransactionDate() throws Exception {
        String timestamp = Instant.now().atOffset(ZoneOffset.UTC).minusSeconds(AppConstants.TRANSACTION_EXPIRED_OFFSET + 60).toString();
        Transaction transaction = createTransaction("2.00", timestamp);
        Mockito.doThrow(new TransactionTimedoutException("Transaction is older than "+AppConstants.TRANSACTION_EXPIRED_OFFSET +" seconds ")).when(transactionService).create(Mockito.any(Transaction.class));
        mockMvc.perform(post(AppConstants.PATH_TRANSACTION).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().is(HttpServletResponse.SC_NO_CONTENT));
    }

    @Test
    public void testRemoveTransactions() throws Exception {
        mockMvc.perform(delete(AppConstants.PATH_TRANSACTION).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString("")))
                .andExpect(status().is(HttpServletResponse.SC_NO_CONTENT));
    }

    private Transaction createTransaction(String amount, String timestamp) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTimestamp(timestamp);

        return transaction;
    }
}
