# kafka-with-quarkus

This project uses Quarkus and demonstrates **Kafka messaging with web interface capabilities**.

This application combines:
- 🔄 **Kafka messaging** - Send and receive messages through Kafka topics
- 🌐 **Web interface** - Interactive web UI to manage messaging
- 🚀 **REST API** - RESTful endpoints for integration


## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## 🌐 Web Interface & API Endpoints

Once the application is running, you can access it through your web browser:

### **Main Web Interface**
- **URL**: <http://localhost:8080/api/messaging/>
- **Features**:
  - Interactive form to send messages to Kafka topics
  - Real-time status feedback
  - Beautiful, responsive UI
  - API documentation

### **REST API Endpoints**

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/messaging/` | Web interface (HTML) |
| `GET` | `/api/messaging/health` | Application health check (JSON) |
| `POST` | `/api/messaging/send` | Send message to Kafka topic (JSON) |

### **API Usage Examples**

**Check application health:**
```bash
curl http://localhost:8080/api/messaging/health
```

**Send a message to Kafka:**
```bash
curl -X POST \
  http://localhost:8080/api/messaging/send \
  -H "Content-Type: application/json" \
  -d "Your message here"
```

**Response format:**
```json
{
  "result": "SUCCESS",
  "message": "Message sent to Kafka topic",
  "data": "Your message here"
}
```

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/kafka-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Dependencies & Features

This application uses the following Quarkus extensions:

- **Messaging - Kafka Connector** ([guide](https://quarkus.io/guides/kafka-getting-started)): Connect to Kafka with Reactive Messaging
- **REST** ([guide](https://quarkus.io/guides/rest)): Build REST services with JAX-RS and RESTEasy Reactive
- **CDI** (Arc): Context and Dependency Injection for managing application beans

## Application Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Browser   │───▶│ MessagingController │───▶│  Kafka Topic    │
│                 │    │   (REST API)    │    │    (words)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                       │
                                ▼                       ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │ MyMessagingApp  │◀───│  Message Flow   │
                       │ (Kafka Consumer)│    │ (words-in/out)  │
                       └─────────────────┘    └─────────────────┘
```

## Related Guides

## How It Works

### **Kafka Messaging Flow**
1. **Message Input**: Submit messages via web interface or REST API
2. **Kafka Producer**: Messages are sent to the `words` Kafka topic
3. **Message Processing**: Application consumes messages, converts to uppercase
4. **Console Output**: Processed messages are printed to console with `>>` prefix

### **Message Processing Components**

#### **MyMessagingApplication.java**
- Produces initial messages on startup: `["Hello", "with", "Quarkus", "Messaging", "message"]`
- Consumes from `words-in` channel and converts to uppercase
- Outputs processed messages to console

#### **MessagingController.java** ✨ *NEW*
- Provides web interface for interactive message sending
- Exposes REST API endpoints for integration
- Handles user input and forwards to Kafka topics

### **Getting Started**

1. **Start the application**:
   ```bash
   ./mvnw quarkus:dev
   ```

2. **Open your browser**: <http://localhost:8080/api/messaging/>

3. **Send messages**: Use the web form or API endpoints

4. **Monitor output**: Watch your terminal for processed messages with `>>` prefix

---

**📖 Related Guides:**
- [Quarkus Kafka Reactive Messaging](https://quarkus.io/guides/kafka-reactive-getting-started)
- [Quarkus REST Services](https://quarkus.io/guides/rest)
