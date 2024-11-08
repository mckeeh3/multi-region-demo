# multi-region-demo

## Run Locally

```bash
mvn compile exec:java
```

If you don't have maven installed, you can run a Docker image that contains maven and Java 21:

```bash
#Windows
docker run -it --rm --name test-project -v %cd%:/usr/src/mymaven -w /usr/src/mymaven  maven:3.9.9-eclipse-temurin-21-alpine mvn compile exec:java

#Linux
docker run -it --rm --name test-project -v $(pwd):/usr/src/mymaven -w /usr/src/mymaven  maven:3.9.9-eclipse-temurin-21-alpine mvn compile exec:java
```

`Ctrl+C` to stop.

## Run Gatling Tests

There are two Gatling tests. One test hits a simple endpoint that returns HTTP responses without invoking other Akka components.
A second test sends randomly generated create user requests. This second test invokes an event-sourced entity via an endpoint component.
Bash scripts are provided for running both tests.

### Run the HTTP only simple Gatling test

```bash
./bin/gatling-test-simple-users.sh [hosthane]
```

The `hostname` is an optional parameter. Default hostname for local testing on host `localhost`, port `9000`.

### Run the create users Gatling test

```bash
./bin/gatling-test-create-users.sh [hostname]
```

The `hostname` is an optional parameter. Default hostname for local testing on host `localhost`, port `9000`.

### Notes

- Make sure your application is running and accessible either locally or on the Akka platform before starting the test.
- The test results will be generated in the `target/gatling` directory after the test run completes.

### Lookup Gatling Hostname

The Gatling simulation defaults to running the app locally.

Use the following command to get the current service host"

```bash
akka routes get multi-region-demo
```

Example response:

```bash
Route:          multi-region-demo
Host:           damp-mode-6178.aws-us-east-2.apps.akka.st
Allow CORS origins:     *
Allow CORS methods:     GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS

Paths:
         /   multi-region-demo

Status:
        HostValidation: True
                Last Transition:        Thu Oct 31 08:29:27 2024
        Ready: True
                Last Transition:        Mon Nov  4 08:49:55 2024
```

### Run Gatling script using Akka service hostname

Copy/paste the above `Host:` and assign is to a variable, then run the Gatling script.

```bash
export host=https://github.com/mckeeh3/multi-region-demo.git
./bin/gatling-test-create-users.sh $host
```

### Gatling script configuration file

A configuration file is used to change the behavior of tests.
The configuration file is located in `src/test/resources/create-users-simulation.conf`.

Here are the setting provided in the configuration file.

```code
# Percent of requests that create users
percentCreate = 10
# Percent of requests that update users
percentUpdate = 30
# Percent of requests that read users
percentRead = 60

# Simulated user pause time after performing an operation
pauseDuration = 500ms

# Requests per second ramp up from 0 to this value over 1 minute
reachRps = 1000
# Hold the RPS at this value for this duration, e.g. 3m, 500s, 1000ms
holdFor = 3m
```

Edit the configuration settings to adjust Gatling tests.

### View Gatling Report After Test Run

At the conclusion of a Gatling test the following message is given that provides the location of the test run report.

```bash
Reports generated, please open the following file: file:///.../index.html
```

Copy the URL to view in a browser or view in an IDE.

## Create and view data using CLI scripts

Two scripts are included in this project for creating and viewing user entities.

Use the `create-user.sh` script to create a user.

Create user with locally running service.

```bash
./bin/create-user.sh user456 user456 user456.example.com
```

Create user with Akka platform service.

```bash
./bin/create-user.sh $host user3 user3 user3@gmail.com
```

The script takes three parameters: userId, name, and email.

Use the `get-user,sh` script to view existing user entities.

```bash
./bin/get-user.sh user456
./bin/get-user.sh $host user456
```

## Query the view

Shell scripts are provided for doing view queries.

Query a single user by Id.

```bash
./bin/get-user-by-id-from-view.sh user-id-1234
./bin/get-user-by-id-from-view.sh $host user-id-1234
```

Query a single user by email.

```bash
./bin/get-user-by-email-from-view.sh user-id-1234@gmail.com
./bin/get-user-by-email-from-view.sh $host user-id-1234@gmail.com
```

Query users with a streaming response. CTRL-c to stop.

```bash
./bin/get-all-users-from-view.sh
./bin/get-all-users-from-view.sh $host
```
