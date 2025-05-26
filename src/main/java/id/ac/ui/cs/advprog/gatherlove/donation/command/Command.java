package id.ac.ui.cs.advprog.gatherlove.donation.command;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface Command<T> {
    CompletableFuture<T> execute();
}