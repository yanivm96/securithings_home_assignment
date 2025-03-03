package com.securithings.assignment.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.securithings.assignment.logger.LoggerMessage;
import com.securithings.assignment.logger.Logger;


public class LogsQueueManager implements Runnable
{
    private static volatile LogsQueueManager instance;
    private final BlockingQueue<LoggerMessage> logsMessagesQueue = new LinkedBlockingQueue<>(); // using LinkedBlockingQueue to manage the writing of logs messages , its thread-safe and efficient.
    private Thread loggerThread;
    private boolean isRunning = true;
    
    private LogsQueueManager() {
        startShutdownHook();
    }

    // return the singleton instance of the LogsQueueManager
    public static LogsQueueManager getInstance() {
        if (instance == null) {
            synchronized (LogsQueueManager.class) {
                if (instance == null) {
                    instance = new LogsQueueManager();
                    instance.startLoggerThread();
                }
            }
        }
        return instance;
    }

    // manage the loggers messages queue in a separate thread
    private void startLoggerThread() {
        loggerThread = new Thread(this);
        loggerThread.setDaemon(true); 
        loggerThread.start();
    }

    // add a new message to the queue
    public void addLoggerMessage(String message, Logger logger)
    {
        if(!this.isRunning)
        {return;}
        try {
            this.logsMessagesQueue.put(new LoggerMessage(message, logger));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // process all remaining messages in the queue when the application is shutting down
    private void processRemainingMessages() {
        this.isRunning = false;
        System.out.println("Processing remaining messages in the queue...");
        while (!logsMessagesQueue.isEmpty()) {
            try {
                LoggerMessage logMessage = logsMessagesQueue.take();
                logMessage.getLogger().writeLog(logMessage.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // add a shutdown hook to process all remaining messages in the queue when the application is shutting down
    private void startShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            processRemainingMessages();
        }));
    }

    // process all messages in the queue
    @Override
    public void run() {
        while (this.isRunning) {
            try {
                LoggerMessage logMessage = logsMessagesQueue.take();
                logMessage.getLogger().writeLog(logMessage.getMessage());
            } 
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                this.isRunning = false;
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                e.printStackTrace(); 
            }
        }
    }
}
