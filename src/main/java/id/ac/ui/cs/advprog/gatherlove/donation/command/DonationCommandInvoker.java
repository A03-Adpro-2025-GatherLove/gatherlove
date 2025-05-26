package id.ac.ui.cs.advprog.gatherlove.donation.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component; // Atau @Service
import java.util.concurrent.CompletableFuture;

@Component // Menjadikannya Spring bean agar bisa di-inject
public class DonationCommandInvoker implements CommandInvoker {

    private static final Logger logger = LoggerFactory.getLogger(DonationCommandInvoker.class);

    @Override
    public <T> CompletableFuture<T> executeCommand(Command<T> command) {
        logger.info("Executing command: {}", command.getClass().getSimpleName());
        // Di sini kita bisa menambahkan logic tambahan sebelum/sesudah eksekusi
        // Misalnya, logging, metrics, dll.
        CompletableFuture<T> result = command.execute();
        result.whenComplete((res, ex) -> {
            if (ex != null) {
                logger.error("Command {} failed: {}", command.getClass().getSimpleName(), ex.getMessage());
            } else {
                logger.info("Command {} completed successfully", command.getClass().getSimpleName());
            }
        });
        return result;
    }
}