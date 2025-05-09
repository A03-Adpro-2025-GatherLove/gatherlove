package id.ac.ui.cs.advprog.gatherlove.donation.command;

@FunctionalInterface
public interface Command<T> {
    T execute();
}