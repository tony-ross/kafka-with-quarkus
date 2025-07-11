# kafka-with-quarkus

This project uses Quarkus and demonstrates **Kafka messaging with web interface capabilities**.

This application combines:
- 🔄 **Kafka messaging** - Send and receive messages through Kafka topics
- 🌐 **Web interface** - Interactive web UI to manage messaging
- 🚀 **REST API** - RESTful endpoints for integration

## ⚠️ Important: Native Compilation Challenges on Mac ARM

**🎯 Recommendation: Use JVM Mode for this application**

Based on extensive testing, we **strongly recommend running this application in JVM mode** rather than attempting native compilation. Here's why:

### **Native Compilation Issues Encountered:**

1. **🏗️ Architecture Mismatch**
   - Container-based builds produce Linux binaries incompatible with macOS
   - Results in `exec format error` when attempting to run

2. **🔧 GraalVM Requirements**
   - Requires specific GraalVM version (23.1.0+) for Quarkus 3.20.1
   - Complex installation and configuration needed
   - Version compatibility issues between GraalVM and Quarkus

3. **🌐 Complex Dependencies**
   - Kafka client libraries have intricate native compilation requirements
   - Netty framework causes class initialization conflicts
   - Error: `Classes that should be initialized at run time got initialized during image building`

4. **📦 Netty/Kafka Incompatibilities**
   - Multiple buffer allocation classes fail native compilation
   - Complex reflection configuration needed
   - Unpredictable build failures with dependency updates

### **✅ JVM Mode Benefits:**

| Feature | JVM Mode ✅ | Native Mode ❌ |
|---------|-------------|----------------|
| **Startup Time** | ~2-3 seconds | ~0.1 seconds |
| **Build Complexity** | Simple | Very Complex |
| **Kafka Support** | Perfect | Problematic |
| **Development** | Hot reload | Requires rebuild |
| **Memory Usage** | Moderate | Lower |
| **Debugging** | Full support | Limited |
| **Reliability** | High | Unpredictable |

### **🚀 Recommended Approach:**

```bash
# For development
./mvnw quarkus:dev

# For production
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

**Result**: Fast startup (~2-3 seconds), perfect Kafka integration, and reliable operation.

---

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

## Creating a native executable ⚠️

> **⚠️ WARNING**: Native compilation is **NOT RECOMMENDED** for this Kafka-based application. See the [Important section](#️-important-native-compilation-challenges-on-mac-arm) above for detailed reasons.

If you still want to attempt native compilation (not recommended), you can try:

```shell script
# Requires GraalVM 23.1.0+ - often fails with Kafka dependencies
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
# Produces Linux binary - will NOT work on macOS
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

**Expected Issues:**
- `exec format error` on macOS (Linux binary produced)
- Netty class initialization failures
- Complex reflection configuration requirements
- Unpredictable build failures

**Instead, use the recommended JVM approach:**
```bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

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

#### **🚀 Recommended: JVM Mode (Production)**

1. **Build and run the application**:
   ```bash
   ./mvnw package
   java -jar target/quarkus-app/quarkus-run.jar
   ```

2. **Open your browser**: <http://localhost:8080/api/messaging/>

3. **Send messages**: Use the web form or API endpoints

4. **Monitor output**: Watch your terminal for processed messages with `>>` prefix

#### **🔧 Alternative: Development Mode**

1. **Start in development mode** (with hot reload):
   ```bash
   ./mvnw quarkus:dev
   ```

2. **Access the application**: <http://localhost:8080/api/messaging/>

**Note**: Development mode is perfect for coding and testing, but use JVM mode for production deployments.

---

**📖 Related Guides:**
- [Quarkus Kafka Reactive Messaging](https://quarkus.io/guides/kafka-reactive-getting-started)
- [Quarkus REST Services](https://quarkus.io/guides/rest)
