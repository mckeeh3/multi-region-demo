# multi-region-demo

## Run Locally

```bash
mvn compile exec:java
```

If you don't have maven installed, you can run a Docker image that contains maven and Java 21:
```
#Windows
docker run -it --rm --name test-project -v %cd%:/usr/src/mymaven -w /usr/src/mymaven  maven:3.9.9-eclipse-temurin-21-alpine mvn compile exec:java

#Linux
docker run -it --rm --name test-project -v $(pwd):/usr/src/mymaven -w /usr/src/mymaven  maven:3.9.9-eclipse-temurin-21-alpine mvn compile exec:java
```

`Ctrl+C` to stop.

## Run Gatling

```bash
mvn gatling:test
```

### Optional Gatling parameters

You can run the Gatling test using Maven. The test supports two optional system properties to customize the test environment:

1. `baseUrl`: The base URL of the application under test (default: "http://localhost:9000")
2. `endpoint`: The specific endpoint to test (default: "/user")

The `baseUrl` parameter is used for local testing. When testing this service on the Akka platform, the `baseUrl` parameter must be set to the service HTTP protocol and hostname.

```bash
mvn gatling:test -DbaseUrl=<service-protocol-hostname>
```

The `endpoint` parameter must be set to either `/user` or `/simple`. When the parameter is set to `/user`, the test will
generate random user entities. When the parameter is set to `/simple`, no user entities are created.

```bash
mvn gatling:test -Dendpoint=</user|/simple>
```

### Notes

- If you don't specify the `baseUrl` or `endpoint` properties, the test will use the default values.
- Make sure your application is running and accessible at the specified base URL before starting the test.
- The test results will be generated in the `target/gatling` directory after the test run completes.

## Change Gatling Hostname

The Gatling simulation defaults to running the app locally.

Use the following to change Gatling to the app running on the platform:

```bash
mvn gatling:test -DbaseUrl=http://the-platform-url
```

TODO provide CLI to obtain the platform URL

## View Gatling Report After Test Run

At the conclusion of a Gatling test the following message is given that provides the location of the test run report.

```bash
Reports generated, please open the following file: file:///.../index.html
```

Copy the URL to view in a browser or view in an IDE.

## Create and view data using scripts

Two scripts are included in this project for creating and viewing user entities.

Use the `create-user.sh` script to create a user.

```bash
./bin/create-user.sh user456 user456 user456.example.com
```

The script takes three parameters: userId, name, and email.

Use the `get-user,sh` script to view existing user entities.

```bash
./bin/get-user.sh user456
```
