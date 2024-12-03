package com.example.ms_transaction.model;

import java.util.*;
import java.util.function.Consumer;


public class TransactionNotifier {
    private final List<Consumer<Transaction>> observers = new ArrayList<>();

    public void registerObserver(Consumer<Transaction> observer) {
        observers.add(observer);
    }

    public void notifyObservers(Transaction transaction) {
        observers.forEach(observer -> observer.accept(transaction));
    }
}
