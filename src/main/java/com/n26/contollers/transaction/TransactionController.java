package com.n26.contollers.transaction;

import com.n26.AppConstants;
import com.n26.dto.Transaction;
import com.n26.exceptions.InvalidRequestException;
import com.n26.exceptions.TransactionTimedoutException;
import com.n26.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping(path = AppConstants.PATH_TRANSACTION, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> doTransaction(@RequestBody @Valid Transaction transaction, BindingResult result) {
        if(result.hasErrors()) {
            throw new InvalidRequestException(result.toString());
        }
        transactionService.create(transaction);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(path =  AppConstants.PATH_TRANSACTION)
    public ResponseEntity<Void> deleteAllTransactions() {
        transactionService.delete();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void TransactionTimedoutExceptionHandler(TransactionTimedoutException ex) {
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    private void InvalidRequestExceptionHandler(InvalidRequestException ex) {
    }

    @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private void resolveException() {
    }

}
