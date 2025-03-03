package com.securithings.assignment.logger;

public class LoggerMessage {
    private final String message;
    private final Logger logger;
    
    public LoggerMessage(final String message, final Logger logger)
    {
        this.message = message;
        this.logger = logger;
    }

    public String getMessage()
    {
        return message;
    }

    public Logger getLogger()
    {
        return logger;
    }
}
