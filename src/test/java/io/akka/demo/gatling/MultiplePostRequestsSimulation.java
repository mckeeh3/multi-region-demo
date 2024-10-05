package io.akka.demo.gatling;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class MultiplePostRequestsSimulation extends Simulation {

  private static final String baseUrl = System.getProperty("baseUrl", "http://localhost:9000");

  private HttpProtocolBuilder httpProtocol = http
      .baseUrl(baseUrl)
      .acceptHeader("application/json")
      .contentTypeHeader("application/json");

  private Iterator<Map<String, Object>> randomFeeder = Stream.generate((Supplier<Map<String, Object>>) () -> {
    Random rand = new Random();
    String randomId = String.valueOf(rand.nextInt(1000));
    String randomName = "User" + rand.ints(97, 123)
        .limit(5)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    String randomEmail = "user" + rand.nextInt(1000) + "@example.com";

    return Collections.unmodifiableMap(Map.of(
        "randomId", randomId,
        "randomName", randomName,
        "randomEmail", randomEmail));
  }).iterator();

  private ScenarioBuilder scn = scenario("Multiple POST Requests")
      .feed(randomFeeder)
      .repeat(10).on(
          exec(http("Create User")
              .post("/user")
              .body(StringBody(
                  "{\"userId\": \"#{randomId}\", \"name\": \"#{randomName}\", \"email\": \"#{randomEmail}\"}"))
              .check(status().is(200)))
                  .pause(Duration.ofSeconds(1)));

  {
    setUp(
        scn.injectOpen(rampUsers(50).during(Duration.ofSeconds(30)))).protocols(httpProtocol);
  }
}
