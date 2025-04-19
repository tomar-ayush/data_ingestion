# Data Ingestion Backend

This is a Spring Boot application for handling data ingestion from flatfiles to ClickHouse database.

## Technology Stack

- Java
- Spring Boot 2.7.5
- Spring Security
- Spring Data JPA
- ClickHouse JDBC (0.3.2)
- PostgreSQL
- OpenCSV for CSV processing
- JWT for authentication

## Prerequisites

- Java 8 or higher
- Maven
- ClickHouse database instance
- PostgreSQL database instance

## Getting Started

1. Clone the repository
2. Navigate to the backend directory
3. Configure the database in `src/main/resources/application.properties`

## Build and Run

To build the application:

```bash
mvn clean package
```

To run the application:

```bash
java -jar target/clickhouse-flatfile-ingestion-1.0.0.jar
```

Alternatively, you can use Spring Boot's Maven plugin:

```bash
mvn spring-boot:run
```

## API Endpoints

The application provides REST API endpoints for:

- File ingestion
- Data transformation
- ClickHouse database operations
- Authentication and authorization

## Features

- CSV file processing with OpenCSV
- Data validation and transformation
- Secure API with JWT authentication
- Integration with ClickHouse for high-performance analytics
- PostgreSQL for metadata storage

## Testing

Run tests using Maven:

```bash
mvn test
```

## Project Structure

- `com.yourorg.ingestion` - Main application package
  - `config` - Configuration classes
  - `controller` - REST API controllers
  - `dto` - Data Transfer Objects
  - `service` - Business logic
  - `repository` - Data access layer
  - `model` - Domain models