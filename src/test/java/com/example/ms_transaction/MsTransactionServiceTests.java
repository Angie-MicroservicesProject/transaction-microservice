package com.example.ms_transaction;


import com.example.ms_transaction.model.Transaction;
import com.example.ms_transaction.repository.TransactionRepository;
import com.example.ms_transaction.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MsTransactionServiceTests {


	@Mock
	private TransactionRepository transactionRepository;

	@InjectMocks
	private TransactionServiceImpl transactionService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetTransaction() {
		String transactionId = "12345";
		Transaction transaction = new Transaction(transactionId, Transaction.TransactionType.DEPOSITO, BigDecimal.TEN, LocalDateTime.now(), "account1", null);

		when(transactionRepository.findById(transactionId)).thenReturn(Mono.just(transaction));

		Mono<Transaction> result = transactionService.getTransaction(transactionId);

		StepVerifier.create(result)
				.expectNext(transaction)
				.verifyComplete();

		verify(transactionRepository, times(1)).findById(transactionId);
	}


	@Test
	void testDeleteTransaction() {
		String transactionId = "12345";

		when(transactionRepository.deleteById(transactionId)).thenReturn(Mono.empty());

		Mono<Void> result = transactionService.deleteTransaction(transactionId);

		StepVerifier.create(result)
				.verifyComplete();

		verify(transactionRepository, times(1)).deleteById(transactionId);
	}

	@Test
	void testRegisterDeposit() {
		String cuentaOrigenId = "account1";
		BigDecimal monto = BigDecimal.valueOf(100);
		Transaction transaction = Transaction.builder()
				.cuentaOrigenId(cuentaOrigenId)
				.tipo(Transaction.TransactionType.DEPOSITO)
				.monto(monto)
				.fecha(LocalDateTime.now())
				.build();

		when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transaction));

		Mono<Transaction> result = transactionService.registerDeposit(cuentaOrigenId, monto);

		StepVerifier.create(result)
				.expectNextMatches(t -> t.getCuentaOrigenId().equals(cuentaOrigenId) && t.getMonto().equals(monto))
				.verifyComplete();

		verify(transactionRepository, times(1)).save(any(Transaction.class));
	}


	@Test
	void testRegisterWithdrawalWithSufficientBalance() {
		String cuentaOrigenId = "account1";
		BigDecimal monto = BigDecimal.valueOf(50);
		List<Transaction> transactions = Arrays.asList(
				new Transaction("1", Transaction.TransactionType.DEPOSITO, BigDecimal.valueOf(100), LocalDateTime.now(), cuentaOrigenId, null)
		);

		when(transactionRepository.findAllByCuentaOrigenId(cuentaOrigenId)).thenReturn(Flux.fromIterable(transactions));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(
				new Transaction("2", Transaction.TransactionType.RETIRO, monto, LocalDateTime.now(), cuentaOrigenId, null)
		));

		Mono<Transaction> result = transactionService.registerWithdrawal(cuentaOrigenId, monto);

		StepVerifier.create(result)
				.expectNextMatches(t -> t.getTipo() == Transaction.TransactionType.RETIRO && t.getMonto().equals(monto))
				.verifyComplete();

		verify(transactionRepository, times(1)).findAllByCuentaOrigenId(cuentaOrigenId);
		verify(transactionRepository, times(1)).save(any(Transaction.class));
	}

	@Test
	void testRegisterWithdrawalWithInsufficientBalance() {
		String cuentaOrigenId = "account1";
		BigDecimal monto = BigDecimal.valueOf(150);
		List<Transaction> transactions = Arrays.asList(
				new Transaction("1", Transaction.TransactionType.DEPOSITO, BigDecimal.valueOf(100), LocalDateTime.now(), cuentaOrigenId, null)
		);

		when(transactionRepository.findAllByCuentaOrigenId(cuentaOrigenId)).thenReturn(Flux.fromIterable(transactions));

		Mono<Transaction> result = transactionService.registerWithdrawal(cuentaOrigenId, monto);

		StepVerifier.create(result)
				.expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException && throwable.getMessage().equals("Saldo insuficiente para el retiro"))
				.verify();

		verify(transactionRepository, times(1)).findAllByCuentaOrigenId(cuentaOrigenId);
		verify(transactionRepository, never()).save(any(Transaction.class));
	}



	@Test
	void testGetTransactionHistory() {
		List<Transaction> transactions = Arrays.asList(
				new Transaction("1", Transaction.TransactionType.DEPOSITO, BigDecimal.TEN, LocalDateTime.now(), "account1", null),
				new Transaction("2", Transaction.TransactionType.RETIRO, BigDecimal.ONE, LocalDateTime.now(), "account1", null)
		);

		when(transactionRepository.findAll()).thenReturn(Flux.fromIterable(transactions));

		Flux<Transaction> result = transactionService.getTransactionHistory();

		StepVerifier.create(result)
				.expectNextCount(2)
				.verifyComplete();

		verify(transactionRepository, times(1)).findAll();
	}


}
