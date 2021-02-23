package com.n26.contollers.statistic;

import com.n26.AppConstants;
import com.n26.dto.Statistics;
import com.n26.exceptions.StatisticsNotFoundException;
import com.n26.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticController {
    @Autowired
    TransactionRepository transactionService;

    @GetMapping(path =  AppConstants.PATH_STATISTICS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Statistics getStatistics() {
        return  transactionService.getStatistics();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void StatisticsNotFoundExceptionHandler(StatisticsNotFoundException ex) {
    }
}
