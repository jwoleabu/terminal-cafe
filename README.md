## Virtual Cafe

The virtual cafe is a multithreaded cafe that serves tea and coffee to customers, clients can join the cafe server, order tea and coffee, check the status of their orders and collect their items.

The cafe logs the events to the terminal and a json file.

## Getting started

### Dependencies
This project requires a Java Development Kit to compile and run.

Gson

### How to compile and run the program

To compile the project run

```
javac -cp ".:gson.jar" Barista.java
javac -cp ".:gson.jar" Customer.java
```


To run the compiled files run the following commands.

Server program command.
```
java -cp ".:gson.jar" Barista
```
Client program command.
```
java -cp ".:gson.jar" Customer
```

### Alternative methods
You could utilize ```netcat``` or the ``nc`` command on linux to represent the client or the server.

The server runs on port 8080 by default so an alternative to server run is.

```
nc -l 0.0.0.0 8080
```

for the client 
```
nc 0.0.0.0 8080
```

## Using the program
Through the customer interface you can utilize keywords like

<ul>
<li>order</li>
<li>status</li>
<li>collect</li>
<li>exit</li>
</ul>

The **order** keyword should be trailed with information about the specific order like "4 teas and 4 coffees" or "1 tea".

**status** shows the client their order status in the format.

status example
```
- 1 tea and 2 coffees in waiting area
- 2 teas and 2 coffees currently being prepared
- 2 teas in tray
```

**collect** retrieves the  users order.

**exit** leaves the cafe.



If you are connecting at a socket using netcat you will have to input requests the exact using the format as the ones below.

## Notable Features

### Json Requests

The server and client use json to communicate
The json is formed of a request type and the data it contains. Using the request type the client and server know exactly what to do with the data provided.

example json requests

```json
{ "type": "JOIN", "data": { "name": "syrup" } }
```
```json
{"type":"ORDER","data":{"items":{"tea":3,"coffee":1}}}
```


### Request Types

<ul>
    <li>JOIN</li>
    <li>LEAVE</li>
    <li>EXIT</li>
    <li>ORDER</li>
    <li>STATUS</li>
    <li>COLLECT</li>
    <li>MESSAGE</li>
    <li>TERMINATE</li>
    <li>ERROR</li>
</ul>

Join: This requires a data property called name containing a string value.

Order: This requires a data property called items containing tea and coffee with their respective counts.

Message or Error: This requires a data property called message containing a string.

### Multithreading
The server utilizing multiple threads to run the cafe and handle client orders.

### Client Server Architecture
The server and clients can be hosted on different processes and communicate via TCP sockets.

### Client UUID
The program can handle users with the same name by generating and using a unique user id under the hood.

### Graceful Shutdowns
The program has shutdown hooks that it can use to 

## Limitations
While it can transfer brewing items from one client to another, currently the program cannot transfer orders in a clients tray from to another.

It loads the orders sequentially which can result in some delay in order confirmation for extremely high quantities.

The server program was built using the Customer program as a standard, when using netcat or sending malformed json requests through other means the server might falter.




