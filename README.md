# 🚀 Distributed Job Scheduling System

A production-grade distributed job scheduling system built using **Spring Boot, Kafka, Redis, MySQL, and Docker Compose**.

This system demonstrates scalable, fault-tolerant, event-driven microservices architecture with distributed locking and retry mechanisms.

---

## 📌 Overview

This project implements a distributed job scheduler that:

- Schedules delayed jobs
- Ensures single execution using Redis distributed locking
- Publishes jobs asynchronously via Kafka
- Executes jobs through independent worker service
- Handles retries and failure scenarios
- Supports Dead Letter Topic (DLT)
- Recovers stuck jobs automatically
- Runs fully containerized using Docker Compose

---

## 🏗 Architecture
```
            +------------------+
            |  Scheduler       |
            |  - Redis Lock    |
            |  - DB Atomic     |
            +------------------+
                      ↓
                 Kafka Topic
                      ↓
            +------------------+
            |  Worker Service  |
            |  - Retry         |
            |  - DLQ           |
            +------------------+
                      ↓
                    MySQL

```

## 🔧 Tech Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- Apache Kafka
- Redis
- MySQL
- Docker & Docker Compose
- Gradle Multi-Module Setup

---

## 🧩 Project Structure
```
job-platform
│
├── job-common # Shared domain module (entities, enums)
├── job-scheduler # Scheduler microservice
├── job-worker # Worker microservice
└── docker-compose.yml
```
## ⚙ Key Engineering Features

### Distributed Locking (Redis)
Prevents duplicate job execution across multiple scheduler instances.

### Atomic DB Status Update
Prevents race conditions when multiple schedulers attempt to pick the same job.

### Event-Driven Execution (Kafka)
Scheduler publishes job events → Worker consumes and executes asynchronously.

### Retry Mechanism
Failed jobs are retried until `maxRetries` limit is reached.

### Dead Letter Topic (DLT)
Jobs exceeding retry limit are moved to DLT for failure isolation.

### Stuck Job Recovery
Jobs stuck in `RUNNING` state are automatically reset to `PENDING`.

### Execution Logging
All job attempts are recorded in `job_execution_logs`.

---

## 🧪 How to Run

### 1️⃣ Clone Repository

```
git clone https://github.com/nishitakoshta/Distributed-Job-Scheduling-System.git
cd job-platform
```
### 2️⃣ Start Infrastructure
```
docker-compose up
```

This starts:
- Kafka
- Redis
- MySQL
- Scheduler
- Worker

---

### 3️⃣ Create a Job

POST to:
```
http://localhost:8080/jobs
```

Sample Request:

```json
{
  "jobName": "emailJob",
  "payload": "Send email to user",
  "scheduledTime": "2026-02-16T10:00:00",
  "maxRetries": 3
}
```

### Observe Execution

* Scheduler publishes job to Kafka
* Worker consumes and executes
* Status updates in DB
* Logs recorded in job_execution_logs

### 📊 Failure Handling Flow

* Job fails during execution
* Retry count increments
* If retryCount < maxRetries → re-published to Kafka
* If retryCount ≥ maxRetries → sent to Dead Letter Topic

### 🔒 Concurrency Safety

* The system prevents duplicate execution using:
* Redis distributed locks
* Atomic DB state transitions
* Idempotent job status checks

### 📈 Scalability

* Multiple scheduler instances supported
* Multiple worker instances supported
* Kafka partitions ensure load distribution
* Horizontal scaling enabled

### 🎯 Learning Outcomes

* This project demonstrates:
* Distributed systems design
* Event-driven architecture
* Fault tolerance patterns
* Retry & DLQ handling
* Distributed locking
* Microservices communication

### 📌 Future Enhancements

* Exponential backoff retry
* Job priority scheduling
* Monitoring & metrics (Micrometer)
* Prometheus + Grafana integration
* REST API for job status tracking
