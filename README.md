# ​ Order Engine - Event-Driven Order Processing System

A simplified **event-driven order processing backend**, built with **Java 21** and **Spring Boot**, simulating the core of an e-commerce platform.  
You can send order-related events (create, pay, ship, cancel) via REST, track order states, and monitor status changes through observer logging and alerts.

---

##  Repository

This project is hosted on GitHub:  
https://github.com/Somtrip/orderEngine.git

---

##  Tech Stack

- **Java 21**  
- **Spring Boot 3.2+** (compatible with Java 21)  
- **Maven** (wrapper included)  
- **Postman** (for API testing)

---

##  Prerequisites

Make sure you have the following installed:

```bash
# Check Java version – should be 21.x
java -version

# Example output:
# openjdk version "21" 2023-09-19

# Maven (if not using the wrapper)
mvn -v

```

## Setup & Run
## 1. Clone the repo
```bash
git clone https://github.com/Somtrip/orderEngine.git
cd orderEngine

```
## 2. Build the project

```bash

./mvnw clean package

```

## 3. Run the application
```bash

./mvnw spring-boot:run

```


The app runs at http://localhost:8080.

## API endpoints

| Method | Endpoint       | Description                         |
| ------ | -------------- | ----------------------------------- |
| POST   | `/events`      | Ingest an event (create, pay, etc.) |
| GET    | `/events`      | List all orders                     |
| GET    | `/events/{id}` | Get specific order by ID            |



## 📬 Testing with Postman
Import Collection
```bash
 # Import the provided Postman collection

   **OrderEngine.postman_collection.json**

```


## In Postman:

1. Click Import

1. Select OrderEngine.postman_collection.json

3. Run requests in order.




## 📂 Event Flow
## 1. Create Order
```bash

{
  "eventId": "e1",
  "timestamp": "2025-07-29T10:00:00Z",
  "eventType": "OrderCreated",
  "orderId": "ORD100",
  "customerId": "CUST100",
  "items": [
    {"itemId": "P001", "qty": 2}
  ],
  "totalAmount": 100.00
}
```



## 2. Partial Payment
```bash

{
  "eventId": "e2",
  "timestamp": "2025-07-29T10:05:00Z",
  "eventType": "PaymentReceived",
  "orderId": "ORD100",
  "amountPaid": 50.00
}

```



## 3. Full Payment
```bash

{
  "eventId": "e3",
  "timestamp": "2025-07-29T10:10:00Z",
  "eventType": "PaymentReceived",
  "orderId": "ORD100",
  "amountPaid": 50.00
}

```


## 4. Schedule Shipping
```bash

{
  "eventId": "e4",
  "timestamp": "2025-07-29T12:00:00Z",
  "eventType": "ShippingScheduled",
  "orderId": "ORD100",
  "shippingDate": "2025-07-30"
}

```



## 5. Cancel Order
```bash

{
  "eventId": "e5",
  "timestamp": "2025-07-29T12:10:00Z",
  "eventType": "OrderCancelled",
  "orderId": "ORD100",
  "reason": "Customer changed mind"
}


```



## Project Structure

```bash
src/main/java/com/som/orderengine
 ├── Application.java
 ├── controller/
 │    └── EventController.java
 ├── domain/
 │    ├── Order.java
 │    └── OrderStatus.java
 ├── events/
 │    ├── BaseEvent.java
 │    ├── OrderCreatedEvent.java
 │    ├── PaymentReceivedEvent.java
 │    ├── ShippingScheduledEvent.java
 │    └── OrderCancelledEvent.java
 ├── processing/
 │    └── EventProcessor.java
 └── observers/
      ├── OrderObserver.java
      ├── LoggerObserver.java
      └── AlertObserver.java

```

## ⚡ Console Output Example

## When you run the app and post events:

```bash

[LOG] Event e1 (OrderCreated) applied to Order ORD100. Status=PENDING
[LOG] Event e2 (PaymentReceived) applied to Order ORD100. Status=PARTIALLY_PAID
[LOG] Event e3 (PaymentReceived) applied to Order ORD100. Status=PAID
[ALERT] Sending alert for Order ORD100: Status changed to PAID
[LOG] Event e4 (ShippingScheduled) applied to Order ORD100. Status=SHIPPED
[ALERT] Sending alert for Order ORD100: Status changed to SHIPPED

```

