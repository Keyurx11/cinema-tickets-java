# Cinema Tickets Service

This project implements a ticket purchasing service for a cinema, based on provided business rules.

## Features

- Enforces strict ticketing rules:
  - Max 25 tickets
  - At least one adult required for any child/infant
  - Infants don't get seats
- Calculates total payment amount and seats to reserve
- Calls external payment and seat reservation services
- Fully covered with unit tests using JUnit 5 and Mockito

## Tech Stack

- Java 21
- Maven
- JUnit 5
- Mockito

## Structure

- `TicketServiceImpl` — core logic
- `TicketOrderValidator` — business rule validation
- `TicketOrderCalculator` — price and seat calculations
- `TicketServiceImplTest` — test coverage for valid and invalid scenarios

## How to Run Tests

```bash
mvn clean test
