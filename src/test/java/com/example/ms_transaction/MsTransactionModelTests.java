package com.example.ms_transaction;

import com.example.ms_transaction.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MsTransactionModelTests {


	@Test
	void testValidarTransaccionRetiroSaldoSuficiente() {
		Transaction transaction = Transaction.builder()
				.tipo(Transaction.TransactionType.RETIRO)
				.monto(BigDecimal.valueOf(50))
				.build();

		assertDoesNotThrow(() -> transaction.validarTransaccion(BigDecimal.valueOf(100)));
	}

	@Test
	void testValidarTransaccionRetiroSaldoInsuficiente() {
		Transaction transaction = Transaction.builder()
				.tipo(Transaction.TransactionType.RETIRO)
				.monto(BigDecimal.valueOf(150))
				.build();

		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> transaction.validarTransaccion(BigDecimal.valueOf(100))
		);

		assertEquals("Saldo insuficiente para realizar la transacción.", exception.getMessage());
	}

	@Test
	void testValidarTransaccionTransferenciaSaldoSuficienteYCuentaDestinoValida() {
		Transaction transaction = Transaction.builder()
				.tipo(Transaction.TransactionType.TRANSFERENCIA)
				.monto(BigDecimal.valueOf(50))
				.cuentaDestinoId("12345")
				.build();

		assertDoesNotThrow(() -> transaction.validarTransaccion(BigDecimal.valueOf(100)));
	}

	@Test
	void testValidarTransaccionTransferenciaSaldoInsuficiente() {
		Transaction transaction = Transaction.builder()
				.tipo(Transaction.TransactionType.TRANSFERENCIA)
				.monto(BigDecimal.valueOf(150))
				.cuentaDestinoId("12345")
				.build();

		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> transaction.validarTransaccion(BigDecimal.valueOf(100))
		);

		assertEquals("Saldo insuficiente para realizar la transacción.", exception.getMessage());
	}

	@Test
	void testValidarTransaccionTransferenciaSinCuentaDestino() {
		Transaction transaction = Transaction.builder()
				.tipo(Transaction.TransactionType.TRANSFERENCIA)
				.monto(BigDecimal.valueOf(50))
				.cuentaDestinoId(null)
				.build();

		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> transaction.validarTransaccion(BigDecimal.valueOf(100))
		);

		assertEquals("La cuenta de destino es obligatoria para una transferencia.", exception.getMessage());
	}

	@Test
	void testValidarTransaccionDeposito() {
		Transaction transaction = Transaction.builder()
				.tipo(Transaction.TransactionType.DEPOSITO)
				.monto(BigDecimal.valueOf(100))
				.build();

		assertDoesNotThrow(() -> transaction.validarTransaccion(BigDecimal.valueOf(100)));
	}

	@Test
	void contextLoads() {

	}



}
