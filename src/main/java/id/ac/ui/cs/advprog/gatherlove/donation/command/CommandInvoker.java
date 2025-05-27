package id.ac.ui.cs.advprog.gatherlove.donation.command;

import java.util.concurrent.CompletableFuture;

public interface CommandInvoker {
    <T> CompletableFuture<T> executeCommand(Command<T> command);
    // Bisa juga ada metode untuk queueing, dll. jika diperlukan di masa depan
    // void scheduleCommand(Command<?> command);
}