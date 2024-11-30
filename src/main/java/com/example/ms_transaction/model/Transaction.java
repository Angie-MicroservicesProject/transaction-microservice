
package com.example.ms_transaction.model;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.mongodb.core.mapping.Document;


import java.math.BigDecimal;
import java.time.LocalDateTime;

    @Getter
    @Setter
    @Builder
    @Document
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @ToString
    public class Transaction {

        @BsonId
        private String id;
        private TransactionType tipo;
        private BigDecimal monto;
        private LocalDateTime fecha;
        private String cuentaOrigenId;
        private String cuentaDestinoId;


        public enum TransactionType {
            DEPOSITO,
            RETIRO,
            TRANSFERENCIA
        }


        public void validarTransaccion(BigDecimal saldoDisponible) {
            if (tipo == TransactionType.RETIRO || tipo == TransactionType.TRANSFERENCIA) {
                if (saldoDisponible == null || saldoDisponible.compareTo(monto) < 0) {
                    throw new IllegalArgumentException("Saldo insuficiente para realizar la transacción.");
                }
            }

            if (tipo == TransactionType.TRANSFERENCIA && (cuentaDestinoId == null || cuentaDestinoId.isEmpty())) {
                throw new IllegalArgumentException("La cuenta de destino es obligatoria para una transferencia.");
            }
        }
    }




