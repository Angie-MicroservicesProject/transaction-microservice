package com.example.ms_transaction;


import com.example.ms_transaction.controller.TransactionController;
import com.example.ms_transaction.model.Transaction;
import com.example.ms_transaction.repository.TransactionRepository;
import com.example.ms_transaction.service.TransactionService;
import com.example.ms_transaction.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class MsTransactionApplicationTests {

	@Mock
	TransactionRepository transactionRepository;

	@Mock
	private TransactionService transactionService;

	@InjectMocks
	private TransactionController transactionController;

	private WebTestClient webTestClient;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		webTestClient = WebTestClient.bindToController(transactionController).build();
	}


	@Test
	void registerDeposit() {
		Transaction transaction = new Transaction("1", Transaction.TransactionType.DEPOSITO, BigDecimal.valueOf(100), LocalDateTime.now(), "123", null);

		when(transactionService.registerDeposit(anyString(), any(BigDecimal.class))).thenReturn(Mono.just(transaction));

		webTestClient.post().uri("/api/transacciones/deposito")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(Map.of("cuentaOrigenId", "123", "monto", "100"))
				.exchange()
				.expectStatus().isOk()
				.expectBody(Transaction.class)
				.isEqualTo(transaction);
	}

	@Test
	void registerTransfer() {
		Transaction transaction = new Transaction("2", Transaction.TransactionType.TRANSFERENCIA, BigDecimal.valueOf(50), LocalDateTime.now(), "123", "456");

		when(transactionService.registerTransfer(anyString(), anyString(), any(BigDecimal.class))).thenReturn(Mono.just(transaction));

		webTestClient.post().uri("/api/transacciones/transferencia")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(Map.of("cuentaOrigenId", "123", "cuentaDestinoId", "456", "monto", "50"))
				.exchange()
				.expectStatus().isOk()
				.expectBody(Transaction.class)
				.isEqualTo(transaction);
	}

	@Test
	void getTransactionHistory() {
		Transaction transaction1 = new Transaction("1", Transaction.TransactionType.DEPOSITO, BigDecimal.valueOf(100), LocalDateTime.now(), "123", null);
		Transaction transaction2 = new Transaction("2", Transaction.TransactionType.RETIRO, BigDecimal.valueOf(50), LocalDateTime.now(), "123", null);

		when(transactionService.getTransactionHistory()).thenReturn(Flux.just(transaction1, transaction2));

		webTestClient.get().uri("/api/transacciones/historial")
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(Transaction.class)
				.contains(transaction1, transaction2);
	}

	@Test
	void viewCustomer() {
		Transaction transaction = new Transaction("1", Transaction.TransactionType.DEPOSITO, BigDecimal.valueOf(100), LocalDateTime.now(), "123", null);

		when(transactionService.getTransaction("1")).thenReturn(Mono.just(transaction));

		webTestClient.get().uri("/api/transacciones/1")
				.exchange()
				.expectStatus().isOk()
				.expectBody(Transaction.class)
				.isEqualTo(transaction);
	}







}
