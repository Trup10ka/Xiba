# Xiba 

Peer-to-Peer application for managing bank accounts.

## Features 
- [x] Create account
- [x] Deposit money
- [x] Withdraw money
- [x] Show bank code
- [x] Show account balance
- [x] Show total bank balance
- [x] Show total number of accounts
- [x] Proxying request to another bank
- [ ] Creating a robbery plan

## Requirements 
- Java 22
- Open port in range `65525 - 65535`, which then you must use to run the server

## Third-party libraries
## **Third-Party Dependencies**

This project relies on several third-party libraries for configuration, logging, utilities, and testing. Below is an overview of the dependencies used:

---

### **1. Configuration**
| Library                                                                 | Description                                   | Version |
|-------------------------------------------------------------------------|-----------------------------------------------|---------|
| [Night-Config (HOCON)](https://github.com/TheElectronWill/night-config) | Parses and manages HOCON configuration files. | `3.8.1` |

---

### **2. Logging**
| Library                            | Description                                                     | Version  |
|------------------------------------|-----------------------------------------------------------------|----------|
| [SLF4J](https://www.slf4j.org/)    | Simple Logging Facade for Java (used as a logging abstraction). | `2.0.16` |
| [Logback](https://logback.qos.ch/) | A powerful and flexible logging framework.                      | `1.5.15` |

---

### **3. Utilities**
| Library                                                                | Description                                                    | Version                        |
|------------------------------------------------------------------------|----------------------------------------------------------------|--------------------------------|
| [JetBrains Annotations](https://github.com/JetBrains/java-annotations) | Provides annotations for nullability and contract enforcement. | `$jetbrainsAnnotationsVersion` |

---

## Setup and installation
You can either clone the project or download JAR from [Downloads](https://github.com/Trup10ka/Xiba/releases/tag/v0.2-aplha.0)

```bash
git clone https://github.com/Trup10ka/Xiba
```

## Configuration

This section explains the available configuration options for the application. Ensure that all values are correctly set before running the program.

---

### **Server Configuration**
| Parameter | Description                                                                          | Example Value |
|-----------|--------------------------------------------------------------------------------------|---------------|
| `host`    | The IP address or hostname where the server will run.                                | `"127.0.0.1"` |
| `port`    | The port number the server will listen on. **Must be within the range 65525–65535.** | `65525`       |

---

### **Timeout Settings**
All timeout values are specified in **milliseconds (MILLIS).**

| Parameter               | Description                                                         | Example Value |
|-------------------------|---------------------------------------------------------------------|---------------|
| `timeouts.client`       | Timeout for user inactivity. If exceeded, the user is disconnected. | `5000`        |
| `timeouts.proxy-client` | Timeout for when the proxy client contacts the remote server.       | `200`         |

---

### **Robbery Plan Settings**
| Parameter                      | Description                                                         | Example Value |
|--------------------------------|---------------------------------------------------------------------|---------------|
| `robbery-plan.subnet-mask`     | The subnet mask used for scanning banks. **Must start with `/`.**   | `"/24"`       |
| `robbery-plan.max-pool-size`   | Maximum number of concurrent tasks when executing the robbery plan. | `50`          |
| `robbery-plan.command-timeout` | Timeout for command execution on remote bank servers.               | `2000`        |

---

### **Account & Port Ranges**
Defines the acceptable ranges for account numbers and ports.

| Parameter                   | Description                                                              | Example Value |
|-----------------------------|--------------------------------------------------------------------------|---------------|
| `ranges.min-account-number` | The minimum allowed account number.                                      | `10000`       |
| `ranges.max-account-number` | The maximum allowed account number.                                      | `99999`       |
| `ranges.min-port`           | The minimum port number for connections. **Must be within 65525–65535.** | `65525`       |
| `ranges.max-port`           | The maximum port number for connections. **Must be within 65525–65535.** | `65535`       |

---

### **Notes**
- **Ensure the port values are in the valid range** (`65525–65535`) to avoid startup failures.
- The **subnet mask** should always be prefixed with `/` (e.g., `/24`).
- Adjust the **timeouts** carefully based on network conditions and system performance.

## How to run
You can run the application using the following command:

```bash
java -jar <name-of-the-jar>.jar
```

## Usage
The JAR app provides very little of console commands. You can use the following commands:

```bash
q - quit the application
```
You have many options for how to connect to the server

You can use `nc` command on Linux or Putty on Windows
```bash
nc -n <ip> <port>
```

## Commands 
| Command | Description                         | Example                             |
|---------|-------------------------------------|-------------------------------------|
| `BC`    | Get bank's IP address               | `BC` → `BC 192.168.1.100`           |
| `AC`    | Create a new account                | `AC` → `AC 12345/192.168.1.100`     |
| `AD`    | Deposit money into an account       | `AD 12345/192.168.1.100 500` → `AD` |
| `AW`    | Withdraw money                      | `AW 12345/192.168.1.100 200` → `AW` |
| `AB`    | Get account balance                 | `AB 12345/192.168.1.100` → `AB 300` |
| `AR`    | Remove an account (if balance is 0) | `AR 12345/192.168.1.100` → `AR`     |
| `BA`    | Get total bank balance              | `BA` → `BA 100000`                  |
| `BN`    | Get total number of clients         | `BN` → `BN 5`                       |

## License
This project is under the MIT License. See the [LICENSE](LICENSE) file for the full license text.
