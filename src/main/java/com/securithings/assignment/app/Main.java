package com.securithings.assignment.app;

import com.securithings.assignment.logger.Logger;
import com.securithings.assignment.writers.ConsoleLogWriter;
import com.securithings.assignment.writers.FileLogWriter;

public class Main {
    public static void main(String[] args) {
        // Create a single logger with both console and file output
        Logger logger = new Logger("MainLogger");
        logger.addLogWriter(new ConsoleLogWriter());
        logger.addLogWriter(new FileLogWriter("logs/application.log"));
        logger.info("Starting application...");
        logger.debug("Debug information");
        logger.error("Sample error message");

        Thread worker = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                logger.info("Processing item " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "WorkerThread");

        // Start and wait for completion
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            logger.error("Main thread interrupted");
        }

        logger.info("Application shutdown complete.");
    }
}