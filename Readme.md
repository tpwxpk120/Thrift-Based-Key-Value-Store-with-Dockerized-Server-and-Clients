# Project: Thrift-Based Key-Value Store with Dockerized Server and Clients

This project implements a multithreaded Thrift-based key-value store with Dockerized server and client applications. The server and clients can be run using Docker containers, and communication happens via Apache Thrift.

## Prerequisites

- **Docker** installed and running on your machine.
- **Java 21 or above** installed on your machine for local compilation.
- **Apache Thrift 0.21.0** and other external libraries included in the `lib` folder.
   - `libthrift-0.21.0.jar`
   - `slf4j-api-1.7.32.jar`
   - `slf4j-simple-1.7.32.jar`
   - `javax.annotation-api-1.3.2.jar`

### Libraries Required (Handled by Docker):

- **Apache Thrift 0.21.0**: For RPC communication between server and clients.
- **SLF4J (Simple Logging Facade for Java)**: For logging, with the `slf4j-api` and `slf4j-simple` libraries.
- **Javax Annotation API 1.3.2**: Used by some of the external libraries.

These libraries are already included in the `lib` folder and handled within the Docker setup.

## i. How to Build the Server and Client

The server and client use external libraries such as **Apache Thrift**. These dependencies are managed by Docker, so you only need to build the Docker images.

### Step 1: Build the Docker Images

To build both the server and client images, run the following commands:

1. **Build the Server Image**:
```bash
docker build -t my-server-image -f Dockerfile.server .
```
   **Build the Client Image**:
```bash
docker build -t my-client-image -f Dockerfile.client .
```
The Dockerfiles are configured to include the necessary libraries from the lib folder during the image creation process.

## ii How to Run the Server and Client in Docker
### Step 1: Create a Docker Network
   First, create a Docker network that will allow the server and clients to communicate:

```bash
docker network create my-network
```
### Step 2: Run the Server

Run the server container using the following command:
```bash
docker run -d --name my-server --network my-network -p 12345:12345 my-server-image
```

* -d: Runs the server in detached mode.
* --network my-network: Connects the server to the Docker network for client communication.
* -p 12345:12345: Exposes port 12345 on your local machine.


### Step 3: Run the Clients
You can now run multiple clients, each with a unique name. Below are examples for running clients:

First Client:
```bash
docker run -it --name my-client --network my-network my-client-image /bin/bash -c "java -cp '/app/classes:/app/lib/*' client.ClientApp my-server 12345 Server1 Client1"
```
Second Client:
```bash
docker run -it --name my-client-2 --network my-network my-client-image /bin/bash -c "java -cp '/app/classes:/app/lib/*' client.ClientApp my-server 12345 Server1 Client2"
```
Repeat this process for any additional clients, using different container and client names.

### Step 4: Interact with the Clients
Once the clients are connected to the server, you can use them to perform operations (e.g., PUT, GET, DELETE) on the server's key-value store.

Stopping and Removing Containers
To stop and remove a running container:
```bash
docker stop <container_name>
docker rm <container_name>
```
For example, to stop and remove the first client:
```bash
docker stop my-client
docker rm my-client
```
To look up which container is running, using following command:
```bash
docker ps
```

## iii How to Build the Server and Client (Locally)
### Step 1:Compile the Java Code (Locally)
To compile both the server and client locally, use the following commands. These commands include the necessary external libraries found in the `lib` folder.
Compile the Server:
```bash
javac -d build/classes -cp lib/libthrift-0.21.0.jar:lib/slf4j-api-1.7.32.jar:lib/slf4j-simple-1.7.32.jar:lib/javax.annotation-api-1.3.2.jar src/main/java/server/*.java src/main/java/gen_java/*.java
```
Compile the Client:
```bash
javac -d build/classes -cp lib/libthrift-0.21.0.jar:lib/slf4j-api-1.7.32.jar:lib/slf4j-simple-1.7.32.jar:lib/javax.annotation-api-1.3.2.jar src/main/java/client/*.java src/main/java/server/*.java src/main/java/gen_java/*.java
```

### Step 2:Run the Server and Client (Locally)
Run the Server:
```bash
java -cp build/classes:lib/libthrift-0.21.0.jar:lib/slf4j-api-1.7.32.jar:lib/slf4j-simple-1.7.32.jar:lib/javax.annotation-api-1.3.2.jar server.ServerApp 12345 server1
```
Run the Client:
```bash
java -cp build/classes:lib/libthrift-0.21.0.jar:lib/slf4j-api-1.7.32.jar:lib/slf4j-simple-1.7.32.jar:lib/javax.annotation-api-1.3.2.jar client.ClientApp localhost 12345 server1
```
We could use following command to check port been used or not:
```bash
lsof -i :12345
```
Since I used 12345 as my port. It will show one IP address with PID.

If 12345 port is been used, we need use following command to take it:
```bash
kill -9 <PID>
```

### Step 3: Running with Gradle
If you have Gradle set up for your project, you can build and run the server and client using the following commands.
```bash
./gradlew runServer
./gradlew runClient
```
## Executive Summary
The goal of this assignment was to design, implement, and deploy a multithreaded key-value store, 
and I used Thrift for communication between a server and multiple clients.
The task involved building both server-side and client-side applications, 
with a focus on enabling Remote Procedure Calls (RPC) through Thrift and ensuring smooth communication over TCP. 
The server needed to handle multiple client requests at the same time, 
which required implementing multi-threading and handling mutual exclusion. 
Another key part of the assignment was containerizing both the server and client applications using Docker, 
which made it easier to manage and scale them. 
This assignment tested my understanding of networking, RPC frameworks, and containerization tools like Docker, 
while also giving me hands-on experience with Gradle for automating builds.

## Technical Impression
This assignment provided a great opportunity to delve deeper into distributed systems, especially the use of Remote Procedure Calls (RPCs). 
Setting up Apache Thrift and ensuring that the server and client communicated correctly was both interesting and challenging. 
It required me to not only define the Thrift service interface, but also generate the code required for the server and client to communicate correctly. 
Implementing multithreading on the server to handle multiple clients simultaneously was another key part, 
which gave me hands-on experience with thread safety and mutexes.
Using Docker to deploy the server and client brought additional complexity. 
I faced several challenges, especially with Docker networking, such as ensuring that all containers were connected to the same network 
and that ports were correctly exposed. Troubleshooting was also required for issues such as dealing with conflicting container names and port bindings. 
These challenges, while frustrating at times, ultimately helped me deepen my understanding of how containers work and how to debug. 
Overall, this assignment was a valuable learning experience in using Docker, managing concurrency, and building service-oriented applications. 
I also encountered a problem. If user 1 used put a 1 and user 2 used put a 2, in this case, under the default settings, a2 will overwrite a1. Should we save two values?
It also showed me the importance of clear logging and debugging, which are essential to ensuring smooth communication between clients and servers in a distributed setting.