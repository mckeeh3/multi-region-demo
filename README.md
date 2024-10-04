# multi-region-demo

## Run Locally

```bash
mvn compile exec:java
```

`Ctrl+C` to stop.

## Run Gatling

```bash
mvn gatling:test
```

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
