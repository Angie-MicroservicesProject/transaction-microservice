package com.example.ms_transaction.service;

import com.example.ms_transaction.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

public interface TransactionService {

    public Mono<Transaction> getTransaction(String id);
    public Mono<Void> deleteTransaction(String id);

    public Mono<Transaction> registerDeposit(String cuentaOrigenId, BigDecimal monto);

    public Mono<Transaction> registerWithdrawal(String cuentaOrigenId, BigDecimal monto);

    public Mono<Transaction> registerTransfer(String cuentaOrigenId, String cuentaDestinoId, BigDecimal monto);

    public Flux<Transaction> getTransactionHistory();



}
