```
graph TD
    %% Styling
    classDef infra fill:#f9f2f4,stroke:#333,stroke-width:2px;
    classDef service fill:#d4edda,stroke:#28a745,stroke-width:2px;
    classDef db fill:#cce5ff,stroke:#007bff,stroke-width:2px;
    classDef event fill:#fff3cd,stroke:#ffc107,stroke-width:2px;
    classDef external fill:#f8d7da,stroke:#dc3545,stroke-width:2px;

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
```
