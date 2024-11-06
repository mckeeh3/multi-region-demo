package io.akka.demo.gatling;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.feed;
import static io.gatling.javaapi.core.CoreDsl.holdFor;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.reachRps;
import static io.gatling.javaapi.core.CoreDsl.responseTimeInMillis;
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

// See README.md for how to run this simulation
public class SimpleUserSimulation extends Simulation {

  private static final Random random = new Random();
  private static final String baseUrl = System.getProperty("baseUrl", "http://localhost:9000");
  private static final String path = "/simple";

  private HttpProtocolBuilder httpProtocol = http
      .baseUrl(baseUrl)
      .acceptHeader("application/json")
      .contentTypeHeader("application/json");

  private Iterator<Map<String, Object>> randomFeeder = Stream.generate((Supplier<Map<String, Object>>) () -> {
    String randomId = String.valueOf(random.nextInt(1_000_000));
    String randomName = "User" + random.ints(97, 123)
        .limit(5)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    String randomEmail = "user" + random.nextInt(1000) + "@example.com";

    return Collections.unmodifiableMap(Map.of(
        "randomId", randomId,
        "randomName", randomName,
        "randomEmail", randomEmail));
  }).iterator();

  private ScenarioBuilder scn = scenario("Multiple POST Requests")
      .repeat(1_000).on(
          feed(randomFeeder)
              .exec(http("Simple User")
                  .post(path)
                  .body(StringBody(
                      "{\"userId\": \"#{randomId}\", \"name\": \"#{randomName}\", \"email\": \"#{randomEmail}\"}"))
                  .check(status().is(200))
                  .check(responseTimeInMillis().lte(5_000)))
              .pause(Duration.ofSeconds(1)));

  {
    setUp(
        scn.injectOpen(
            rampUsers(1_000).during(Duration.ofMinutes(1))))
                .throttle(
                    reachRps(1_000).in(Duration.ofMinutes(1)),
                    holdFor(Duration.ofMinutes(2)),
                    reachRps(0).in(Duration.ofMinutes(1))) // sum the 3 durations for the total test run time
                .protocols(httpProtocol);
  }
}
