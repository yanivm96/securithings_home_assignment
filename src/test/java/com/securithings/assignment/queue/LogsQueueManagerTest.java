package com.securithings.assignment.queue;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import com.securithings.assignment.logger.Logger;
import com.securithings.assignment.logger.LogLevel;
import com.securithings.assignment.writers.LogWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class LogsQueueManagerTest 
{
    private LogsQueueManager queueManager;
    private TestLogger testLogger;
    private TestLogWriter testWriter;

    @BeforeEach
    void setUp() {
        queueManager = LogsQueueManager.getInstance();
        testWriter = new TestLogWriter();
        testLogger = new TestLogger("TestClass");
        testLogger.addLogWriter(testWriter);
    }

    @Test
    void testSingletonInstance() {
        LogsQueueManager instance1 = LogsQueueManager.getInstance();
        LogsQueueManager instance2 = LogsQueueManager.getInstance();
        assertSame(instance1, instance2, "Should return the same instance");
    }

    @Test
    void testMessageProcessing() throws InterruptedException {
        String testMessage = "Test message";
        queueManager.addLoggerMessage(testMessage, testLogger);
        
        
        Thread.sleep(100); // Wait for message processing
        
        assertTrue(testWriter.hasMessages(), "Message should be processed");
        assertEquals(testMessage, testWriter.getLastMessage(), "Message content should match");
    }

    @Test
    void testMultipleMessages() throws InterruptedException {
        int messageCount = 5;
        
        for (int i = 0; i < messageCount; i++) {
            queueManager.addLoggerMessage("Message " + i, testLogger);
        }
        
        Thread.sleep(200); // Wait for all messages to be processed
        
        assertEquals(messageCount, testWriter.getMessageCount(), 
            "All messages should be processed");
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        int threadCount = 10;
        int messagesPerThread = 100;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(threadCount);
        
        // Create multiple threads that will add messages concurrently
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    for (int j = 0; j < messagesPerThread; j++) {
                        queueManager.addLoggerMessage(
                            "Message from thread " + threadId + " msg " + j, 
                            testLogger
                        );
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    completionLatch.countDown();
                }
            }).start();
        }
        
        startLatch.countDown(); // Start all threads
        completionLatch.await(5, TimeUnit.SECONDS); // Wait for completion
        Thread.sleep(500); // Wait for queue processing
        
        assertEquals(threadCount * messagesPerThread, testWriter.getMessageCount(), 
            "All messages from all threads should be processed");
    }

    // Helper test classes
    private static class TestLogger extends Logger {
        public TestLogger(String className) {
            super(className, LogLevel.INFO);
        }
    }

    private static class TestLogWriter implements LogWriter {
        private final List<String> messages = new ArrayList<>();

        @Override
        public synchronized void writeLog(String message) {
            messages.add(message);
        }

        public synchronized boolean hasMessages() {
            return !messages.isEmpty();
        }

        public synchronized String getLastMessage() {
            return messages.isEmpty() ? null : messages.get(messages.size() - 1);
        }

        public synchronized int getMessageCount() {
            return messages.size();
        }
    }
}
