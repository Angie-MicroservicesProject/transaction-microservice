package com.example.ms_transaction.controller;


import com.example.ms_transaction.model.Transaction;
import com.example.ms_transaction.repository.TransactionRepository;
import com.example.ms_transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;


    @Autowired
    public TransactionController(TransactionRepository transactionRepository, TransactionService transactionService){
        this.transactionRepository=transactionRepository;
        this.transactionService=transactionService;
    }



    @GetMapping("/transacciones/{id}")
    public Mono<Transaction> viewCustomer(@RequestHeader Map<String, String> headers, @PathVariable String id) {
        return transactionService.getTransaction(id)
                .doOnError(throwable -> System.out.println(throwable));
    }

    @DeleteMapping("/transacciones/{id}")
    public Mono<Void> deleteCustomer(@RequestHeader Map<String, String> headers, @PathVariable String id) {
        return transactionService.deleteTransaction(id);
    }


    @PostMapping("/transacciones/deposito")
    public Mono<Transaction> registerDeposit(@RequestBody Map<String, Object> request) {
        String cuentaOrigenId = (String) request.get("cuentaOrigenId");
        BigDecimal monto = new BigDecimal((request.get("monto").toString()));
        return transactionService.registerDeposit(cuentaOrigenId, monto);
    }

    @PostMapping("/transacciones/retiro")
    public Mono<Transaction> registerWithdrawal(@RequestBody Map<String, Object> request) {
        String cuentaOrigenId = (String) request.get("cuentaOrigenId");
        BigDecimal monto = new BigDecimal(request.get("monto").toString());
        return transactionService.registerWithdrawal(cuentaOrigenId, monto);
    }

    @PostMapping("/transacciones/transferencia")
    public Mono<Transaction> registerTransfer(@RequestBody Map<String, Object> request) {
        String cuentaOrigenId = (String) request.get("cuentaOrigenId");
        String cuentaDestinoId = (String) request.get("cuentaDestinoId");
        BigDecimal monto = new BigDecimal(request.get("monto").toString());
        return transactionService.registerTransfer(cuentaOrigenId, cuentaDestinoId, monto);
    }

    @GetMapping("/transacciones/historial")
    public Flux<Transaction> getTransactionHistory(@RequestHeader Map<String, String> headers) {
        return transactionService.getTransactionHistory();
    }



}
