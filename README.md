# Spring SSE with Redis Pub/Sub

This project demonstrates a simple architecture using **Spring Boot 3**, **Server-Sent Events (SSE)**, and **Redis Pub/Sub** to enable asynchronous and real‑time communication between different services or components. The goal is to serve as a practical Proof of Concept (POC) that showcases how events can be published to Redis and consumed via SSE in an API.

## 🚀 Technologies Used

* **Java 21**
* **Spring Boot 3**
* **Spring Web** (SSE implemented using `SseEmitter`)
* **Redis** (Pub/Sub)
* **Docker & Docker Compose**
* **Spring Actuator**

---

## 📌 Project Purpose

The purpose of this project is to demonstrate the integration between SSE and a messaging system using Redis. This setup allows applications to publish events to a channel while clients receive updates in real time through SSE streams.

---

## 🗂️ Project Structure

```
spring-sse-redis-pub-sub/
│
├── app/src/main/java/...       # Application source code
├── app/src/main/resources/
│   ├── application.yml         # Main configuration
│
├── docker-compose.yml          # Redis and RedisInsight containers
└── README.md                   # Project documentation
```

---

## ⚙️ How to Run the Project

### 1️⃣ Start Redis using Docker

```
docker-compose up -d
```

### 2️⃣ Run the Spring Boot application

```
mvn spring-boot:run
```

---

## 📡 Endpoints

### SSE Stream

```
GET /products/123/events?targetStatus=CONFIRMED
```

Opens a persistent SSE connection that streams updates whenever a status change event is published.

### Publish Status Update Event

```
POST /products/123/status/CONFIRMED
```

Publishes a new status update event to Redis, notifying all connected SSE clients.

---

## 🧪 Local Testing

The project supports local pub/sub communication tests using Docker containers. You can also inspect data and channels using **RedisInsight**.

---

## 🛠️ HAProxy Configuration (optional)

For SSE connections, it is important to adjust timeout settings:

```
timeout client  1h
timeout server  1h
```

Or set very high values for long‑lived connections.

---

## 📄 License

This project is a POC and can be freely used as an example or as a base for other projects.

---

## ✨ Author

Project created for educational purposes by **Relry Pereira**.
