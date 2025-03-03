package com.securithings.assignment.writers;

public class ConsoleLogWriter implements LogWriter
{
    // write the log message to the console
    @Override
    public void writeLog(final String message)
    {
        System.out.println(message);
    }
}