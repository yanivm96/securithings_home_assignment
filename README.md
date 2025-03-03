# securithings_home_assignment

## Introduction

This project is a logging system that includes various components such as loggers, log writers, and a queue manager for asynchronous logging.

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Java Development Kit (JDK) 23 or higher
- Apache Maven 3.9.9 or higher
- Git

## Installation

Follow these steps to set up and run the project:

### 1. Clone the repository

Clone the repository from GitHub to your local machine:

```bash
git clone https://github.com/yanivm96/securithings_home_assignment.git
cd securithings_home_assignment
mvn clean compile exec:java
```

## Configuration

You can change the logs folder output inside the `config.properties` file located in the `src/resources` directory.

## Usage

After configuring the project, you can run it using Maven. The application will start and log messages to both the console and the specified log file.

```bash
mvn exec:java
```

## Running Tests

To run the tests for this project, use the following Maven command:

```bash
mvn test
```
