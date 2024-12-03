package com.example.ms_transaction.service.impl;

import com.example.ms_transaction.model.Transaction;
import com.example.ms_transaction.model.TransactionNotifier;
import com.example.ms_transaction.repository.TransactionRepository;
import com.example.ms_transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public Mono<Transaction> getTransaction(String id) {
        return transactionRepository.findById(id);

    }

    @Override
    public Mono<Void> deleteTransaction(String id) {
        return transactionRepository.deleteById(id);
    }

//    @Override
//    public Mono<Transaction> registerDeposit(String cuentaOrigenId, BigDecimal monto) {
//
//        if (cuentaOrigenId == null || monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
//            return Mono.error(new IllegalArgumentException("Cuenta origen y monto deben ser válidos"));
//        }
//
//        Transaction transaction = Transaction.builder()
//                .cuentaOrigenId(cuentaOrigenId)
//                .tipo(Transaction.TransactionType.DEPOSITO)
//                .monto(monto)
//                .fecha(LocalDateTime.now())
//                .build();
//
//        return transactionRepository.save(transaction);
//    };

    private final TransactionNotifier transactionNotifier = new TransactionNotifier();

    @Override
    public Mono<Transaction> registerDeposit(String cuentaOrigenId, BigDecimal monto) {
        return transactionRepository.save(createDepositTransaction(cuentaOrigenId, monto))
                .doOnSuccess(transaction -> transactionNotifier.notifyObservers(transaction));
    }

    private Transaction createDepositTransaction(String cuentaOrigenId, BigDecimal monto) {
        if (cuentaOrigenId == null || cuentaOrigenId.isEmpty()) {
            throw new IllegalArgumentException("El ID de la cuenta origen no puede ser nulo o vacío.");
        }

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }

        return Transaction.builder()
                .cuentaOrigenId(cuentaOrigenId)
                .monto(monto)
                .tipo(Transaction.TransactionType.DEPOSITO)
                .fecha(LocalDateTime.now())
                .build();
    }



    @Override
    public Mono<Transaction> registerWithdrawal(String cuentaOrigenId, BigDecimal monto) {
        if (cuentaOrigenId == null || monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("Cuenta origen y monto deben ser válidos"));
        }

        // Lógica simulada para validar saldo (sin AccountService)
        return transactionRepository.findAllByCuentaOrigenId(cuentaOrigenId)
                .collectList()
                .flatMap(transactions -> {
                    BigDecimal saldo = calculateBalance(transactions);
                    if (saldo.compareTo(monto) < 0) {
                        return Mono.error(new IllegalArgumentException("Saldo insuficiente para el retiro"));
                    }

                    Transaction transaction = Transaction.builder()
                            .cuentaOrigenId(cuentaOrigenId)
                            .tipo(Transaction.TransactionType.RETIRO)
                            .monto(monto)
                            .fecha(LocalDateTime.now())
                            .build();

                    return transactionRepository.save(transaction);
                });
    }

    @Override
    public Mono<Transaction> registerTransfer(String cuentaOrigenId, String cuentaDestinoId, BigDecimal monto) {
        if (cuentaOrigenId == null || cuentaDestinoId == null || monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("Cuentas y monto deben ser válidos"));
        }

        // Lógica simulada para validar saldo (sin AccountService)
        return transactionRepository.findAllByCuentaOrigenId(cuentaOrigenId)
                .collectList()
                .flatMap(transactions -> {
                    BigDecimal saldo = calculateBalance(transactions);
                    if (saldo.compareTo(monto) < 0) {
                        return Mono.error(new IllegalArgumentException("Saldo insuficiente para la transferencia"));
                    }

                    Transaction transaction = Transaction.builder()
                            .cuentaOrigenId(cuentaOrigenId)
                            .cuentaDestinoId(cuentaDestinoId)
                            .tipo(Transaction.TransactionType.TRANSFERENCIA)
                            .monto(monto)
                            .fecha(LocalDateTime.now())
                            .build();

                    return transactionRepository.save(transaction);
                });
    }




    @Override
    public Flux<Transaction> getTransactionHistory() {
        return transactionRepository.findAll();
    }


    private BigDecimal calculateBalance(Iterable<Transaction> transactions) {
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            if (transaction.getTipo() == Transaction.TransactionType.DEPOSITO) {
                balance = balance.add(transaction.getMonto());
            } else if (transaction.getTipo() == Transaction.TransactionType.RETIRO || transaction.getTipo() == Transaction.TransactionType.TRANSFERENCIA) {
                balance = balance.subtract(transaction.getMonto());
            }
        }
        return balance;
    }
}
