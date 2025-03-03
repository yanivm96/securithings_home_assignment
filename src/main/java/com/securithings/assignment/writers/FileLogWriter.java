package com.securithings.assignment.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileLogWriter implements LogWriter
{
    private final String filePath;
    private boolean isValidPath = false;

    public FileLogWriter(final String filePath)
    {
        this.filePath = filePath;
        ensureFileExists();
    }

    // create the log file if it doesn't exist
    private void ensureFileExists() {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw new IOException("Failed to create parent directories for: " + filePath);
                }
            }
            if (file.exists() || file.createNewFile()) {
                this.isValidPath = true;
            } else {
                throw new IOException("Failed to create log file: " + filePath);
            }

        } catch (IOException e) {
            System.out.println("Error creating log file: " + e.getMessage());
        }
    }

    // write the log message to the file
    @Override
    public void writeLog(final String message)
    {
        if (isValidPath)
        {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) 
            {
                writer.println(message);
            } catch (IOException e) 
            {
                System.out.println("Error writing log to file: " + e.getMessage());
            }
        }
    }
}
