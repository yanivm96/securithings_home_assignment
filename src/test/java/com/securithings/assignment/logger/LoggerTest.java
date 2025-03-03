package com.securithings.assignment.logger;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.securithings.assignment.writers.*;

class LoggerTest {

    private Logger logger;
    private TestLogWriter testWriter;
    
    @BeforeEach
    void setUp() {
        logger = new Logger("TestClass");
        testWriter = new TestLogWriter();
        logger.addLogWriter(testWriter);
    }

    @Test
    void testDefaultLogLevel() {
        assertEquals(LogLevel.INFO, logger.getLevel());
    }

    @Test
    void testSetLogLevel() {
        logger.setLevel(LogLevel.DEBUG);
        assertEquals(LogLevel.DEBUG, logger.getLevel());
    }

    @Test
    void testLogLevelFiltering() {
        logger.setLevel(LogLevel.ERROR);
        
        logger.debug("Debug message");
        assertFalse(testWriter.hasMessages());
        
        logger.info("Info message");
        assertFalse(testWriter.hasMessages());
        
        logger.error("Error message");
        try{
            Thread.sleep(100); // wait for the message to be processed
        } catch (InterruptedException e) {
            fail("Thread interrupted: " + e.getMessage());
        }
        assertTrue(testWriter.hasMessages());
        assertTrue(testWriter.getLastMessage().contains("Error message"));
    }

    @Test
    void testMultipleWriters() {
        TestLogWriter secondWriter = new TestLogWriter();
        logger.addLogWriter(secondWriter);
        logger.info("Test multiple writers");
        
        try{
            Thread.sleep(100); // wait for the message to be processed
        } catch (InterruptedException e) {
            fail("Thread interrupted: " + e.getMessage());
        }
        assertTrue(testWriter.hasMessages());
        assertTrue(secondWriter.hasMessages());
        assertEquals(testWriter.getLastMessage(), secondWriter.getLastMessage());
    }

    @Test
    void testRemoveWriter() {
        logger.removeLogWriter(testWriter);
        logger.info("Test message");
        assertFalse(testWriter.hasMessages());
    }

    @Test
    void testFileLogWriter(@TempDir Path tempDir) {
        String logFile = tempDir.resolve("test.log").toString();
        FileLogWriter fileWriter = new FileLogWriter(logFile);
        logger.addLogWriter(fileWriter);
        
        logger.info("File test message");
        
        try {
            String fileContent = new String(java.nio.file.Files.readAllBytes(tempDir.resolve("test.log")));
            assertTrue(fileContent.contains("File test message"));
        } catch (IOException e) {
            fail("Failed to read log file: " + e.getMessage());
        }
    }

    // Helper class for testing
    private static class TestLogWriter implements LogWriter {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void writeLog(String message) {
            messages.add(message);
        }

        public boolean hasMessages() {
            return !messages.isEmpty();
        }

        public String getLastMessage() {
            return messages.isEmpty() ? null : messages.get(messages.size() - 1);
        }
    }
}