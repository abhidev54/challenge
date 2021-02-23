package com.n26.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.AppConstants;
import com.n26.contollers.statistic.StatisticController;
import com.n26.dto.Statistics;
import com.n26.repositories.TransactionRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class StatisticControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private StatisticController statisticController;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(statisticController).build();
    }

    @Test
    public void testGetStatistics() throws Exception {
        Statistics statistics = Statistics.newBuilder().withCount(3).withSum("40.44").withAvg("4.6").withMin("3.5")
                .withMax("10.40").build();
        Mockito.when(transactionRepository.getStatistics()).thenReturn(statistics);

        mockMvc.perform(get(AppConstants.PATH_STATISTICS).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.count").value(statistics.getCount()))
                .andExpect(jsonPath("$.avg").value(statistics.getAvg()))
                .andExpect(jsonPath("$.sum").value(statistics.getSum()))
                .andExpect(jsonPath("$.min").value(statistics.getMin()))
                .andExpect(jsonPath("$.max").value(statistics.getMax()));
    }
}
