# 🏋️ Gym Management Ecosystem - Microservices Architecture

An enterprise-grade, distributed backend system designed to manage a modern gym chain. Built with **Spring Boot 3** and **Java 17**, this ecosystem leverages **Spring Cloud** for service discovery and routing, **Apache Kafka** for asynchronous event-driven architecture, and **Stripe** for secure payment processing.

## 🏗️ System Architecture

This monorepo contains six independent microservices that communicate securely to handle the entire user journey—from registration to booking to automated billing and notifications.

```mermaid
graph TD
    %% Styling with High Contrast for GitHub Dark/Light Mode
    classDef infra fill:#e2e8f0,stroke:#475569,stroke-width:2px,color:#000000;
    classDef service fill:#dcfce7,stroke:#16a34a,stroke-width:2px,color:#000000;
    classDef db fill:#dbeafe,stroke:#2563eb,stroke-width:2px,color:#000000;
    classDef event fill:#fef08a,stroke:#ca8a04,stroke-width:2px,color:#000000;
    classDef external fill:#fee2e2,stroke:#dc2626,stroke-width:2px,color:#000000;

    Client([📱 Web / Mobile Client]) -->|HTTP REST| Gateway

    subgraph Spring Cloud Infrastructure
        Gateway[API Gateway :8080]:::infra
        Eureka((Eureka Discovery Server :8761)):::infra
        Gateway -.->|Fetch Routes| Eureka
    end

    subgraph Gym Microservices Ecosystem
        Member[Member Service]:::service
        Workout[Workout & Booking Service]:::service
        Payment[Payment Service :8083]:::service
        Notification[Notification Service :8084]:::service
    end

    %% Routing
    Gateway -->|Route Traffic| Member
    Gateway -->|Route Traffic| Workout
    Gateway -->|Route Traffic| Payment

    %% Service Registry
    Member -.->|Register| Eureka
    Workout -.->|Register| Eureka
    Payment -.->|Register| Eureka
    Notification -.->|Register| Eureka

    subgraph External Integrations & Data
        Stripe[(Stripe API)]:::external
        MySQL[(MySQL DB)]:::db
        SMTP[Gmail SMTP Server]:::external
    end

    subgraph Message Broker
        Kafka{Apache Kafka :9092<br/>Topic: payment_success_topic}:::event
    end

    %% The Checkout Flow
    Payment -->|1. Generate Checkout URL| Stripe
    Payment -->|2. Save PENDING Record| MySQL
    Payment -->|3. Publish PaymentEvent| Kafka
    Kafka -->|4. Consume Event| Notification
    Notification -->|5. Dispatch Welcome Email| SMTP
