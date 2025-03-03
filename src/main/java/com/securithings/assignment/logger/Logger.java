package com.securithings.assignment.logger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.securithings.assignment.queue.LogsQueueManager;
import com.securithings.assignment.writers.LogWriter;

public class Logger
{
    private final String className;
    private volatile LogLevel logLevel;
    private final List<LogWriter> logWriters = new ArrayList<>();
    private static final LogsQueueManager loggersMessageManager = LogsQueueManager.getInstance();
    private static final SimpleDateFormat loggerDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS");

    public Logger(final String className)
    {
        this.className = className;
        this.logLevel = LogLevel.INFO;
    }

    public Logger(final String className, final LogLevel logLevel)
    {
        this.className = className;
        this.logLevel = logLevel;
    }
    
    public void setLevel(final LogLevel logLevel)
    {
        this.logLevel = logLevel;
    }

    public LogLevel getLevel()
    {
        return logLevel;
    }

    // add a new log writer e.g. console, file, etc.
    public void addLogWriter(final LogWriter logWriter)
    {
        this.logWriters.add(logWriter);
    }

    // remove a log writer e.g. console, file, etc.
    public void removeLogWriter(final LogWriter logWriter)
    {
        this.logWriters.remove(logWriter);
    }
    
    public String getClassName()
    {
        return className;
    }

    // log a message with the given log level
    private void log(final String message, final LogLevel level)
    {
        if (level.ordinal() >= this.logLevel.ordinal())
        {
            loggersMessageManager.addLoggerMessage(createLogMessage(message, level), this);
        }
    }

    // log a message with INFO log level
    public void info(final String message)
    {
        this.log(message, LogLevel.INFO);
    }

    // log a message with DEBUG log level
    public void debug(final String message)
    {
        this.log(message, LogLevel.DEBUG);
    }

    // log a message with ERROR log level
    public void error(final String message)
    {
        this.log(message, LogLevel.ERROR);
    }

    // write the log message to all the log writers e.g. console, file, etc.
    public void writeLog(final String message)
    {
        for (LogWriter logWriter : logWriters)
        {
            logWriter.writeLog(message);
        }
    }

    // create a log message with the current timestamp, thread id, log level, class name and the message
    private String createLogMessage(final String message, final LogLevel level)
    {
        long threadId = Thread.currentThread().threadId();
        String timestamp = loggerDateFormat.format(new java.util.Date());
        return String.format("%s ([%d]) %s [%s] - %s", timestamp, threadId, level, this.className, message);
    }
}