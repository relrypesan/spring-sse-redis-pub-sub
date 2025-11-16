# Spring SSE with Redis Pub/Sub

This repository presents a complete Proof of Concept (POC) demonstrating **real-time server-side event streaming (SSE)** integrated with **Redis Pub/Sub**, **Spring Boot 3**, **HAProxy load balancing**, and a multi-instance clustered scenario running through **Docker Compose**. The project simulates a distributed architecture where multiple application instances receive and broadcast product status updates.

This updated README contains a deeper explanation of integrations, endpoints, load balancer behavior, and each service in the Docker ecosystem.

---

## 🚀 Technologies Used

* **Java 21**
* **Spring Boot 3**
* **Spring Web** – SSE implemented using `SseEmitter`
* **Spring Data Redis** – Pub/Sub messaging
* **Spring Actuator** – Health/metrics exposure
* **Redis** – Event broadcasting layer
* **RedisInsight** – Monitoring and inspecting Redis
* **HAProxy** – Local load balancing to simulate an NLB
* **Docker & Docker Compose** – Multi-instance environment

---

## 📌 Project Purpose

The objective of this POC is to demonstrate:

* How multiple instances of the same Spring Boot application can receive Redis Pub/Sub messages.
* How clients can subscribe to **real-time SSE streams** based on product status changes.
* How a load balancer (HAProxy) distributes connections across 5 application replicas.
* How to simulate an **AWS NLB-like environment locally**.

This setup reflects real production flows where event-driven updates are required across horizontally scaled services.

---

## 🗂️ Project Structure

```
spring-sse-redis-pub-sub/
│
├── app/
│   ├── src/main/java/...        # Application source code
│   ├── src/main/resources/
│   │   ├── application.yaml     # App-level configuration
│   ├── Dockerfile               # Image build for the app
│
├── haproxy/
│   ├── haproxy.cfg              # Load balancer configuration
│
├── docker-compose.yml           # Multi-service orchestration
└── README.md                    # Documentation
```

---

## ⚙️ Running the Project

### 1️⃣ Build and start all services

```
docker-compose up --build -d
```

This will start:

* 5 application instances (replicas)
* Redis
* RedisInsight
* HAProxy as load balancer

### 2️⃣ Access the application through HAProxy

```
http://localhost:8080
```

HAProxy will route each request to one of the 5 app instances.

### 3️⃣ View logs from all instances

```
docker-compose logs -f app
```

---

## 📡 Endpoints

Below are the main endpoints exposed by the application.

### 🔹 **1. SSE Stream Listener**

```
GET /products/{productId}/events?targetStatus=CONFIRMED
```

**Description:** Opens a persistent Server-Sent Events (SSE) connection.

**Query parameters:**

| Parameter      | Required | Description                                            |
| -------------- | -------- | ------------------------------------------------------ |
| `targetStatus` | **Yes**  | Status the client is waiting for the product to reach. |

When any instance publishes an update with the given status, all connected SSE clients receive the event.

---

### 🔹 **2. Publish Product Status Update**

```
POST /products/{productId}/status/{newStatus}
```

**Description:** Publishes a new status update event to Redis. All running instances receive and rebroadcast updates to SSE subscribers.

**Path variables:**

| Variable    | Required | Description            |
| ----------- | -------- | ---------------------- |
| `productId` | **Yes**  | Product identifier     |
| `newStatus` | **Yes**  | Status to be broadcast |

This is the main triggering mechanism for real-time updates.

---

## 🔁 Redis Pub/Sub Integration

The application connects to Redis and subscribes to a specific channel:

```
product-status-update
```

Whenever any instance receives a POST status update, it publishes to this channel. All 5 running app replicas:

* Receive the message
* Process the event
* Notify SSE clients waiting for updates

This simulates a distributed architecture where all nodes are synchronized through Redis.

---

## 🏷️ HAProxy Load Balancer

The project includes **HAProxy 2.9** configured as a local NLB to distribute load to all 5 app replicas.

### Key Behaviors Implemented

* Round-robin balancing
* Long-lived SSE connections supported
* Timeout increased to allow SSE without interruption

Example config excerpt:

```
timeout client  1h
timeout server  1h
```

These high timeout values avoid SSE disconnections.

---

## 🧰 Docker Compose – Service Overview

Below is a clear description of each service running in the Docker Compose environment.

### **1. app** (5 replicas)

* Runs the Spring Boot application
* Subscribes to Redis Pub/Sub
* Exposes SSE endpoints
* Receives HTTP requests via HAProxy

The replicas simulate horizontal scaling.

### **2. haproxy**

* Distributes traffic between `app` replicas
* Routes SSE connections
* Simulates AWS NLB behavior

### **3. redis**

* Message broker for Pub/Sub
* Stores data using append-only persistence

### **4. redisinsight**

* Web dashboard for monitoring Redis
* Useful to inspect channels, keys, and performance

Access RedisInsight:

```
http://localhost:5540
```

Then add a database connection:

```
host: redis
port: 6379
```

---

## 🧪 Local Testing

### Test the SSE connection

```
curl http://localhost:8080/products/123/events?targetStatus=CONFIRMED
```

### Send a status update

```
curl -X POST http://localhost:8080/products/123/status/CONFIRMADO
```

Your SSE client receives the update instantly.

---

## 📄 License

This project is a POC and can be freely used or adapted.

---

## ✨ Author

Developed by **Relry Pereira** for study and demonstration of real-time event communication architectures.
