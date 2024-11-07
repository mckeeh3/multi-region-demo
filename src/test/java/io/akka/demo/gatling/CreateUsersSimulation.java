package io.akka.demo.gatling;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.feed;
import static io.gatling.javaapi.core.CoreDsl.holdFor;
import static io.gatling.javaapi.core.CoreDsl.percent;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.randomSwitch;
import static io.gatling.javaapi.core.CoreDsl.reachRps;
import static io.gatling.javaapi.core.CoreDsl.responseTimeInMillis;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class CreateUsersSimulation extends Simulation {

  private static final Random random = new Random();
  private static final String baseUrl = System.getProperty("baseUrl", "http://localhost:9000");
  private static final String path = "/user";
  private static final List<String> createdUserIds = Collections.synchronizedList(new ArrayList<>());

  private HttpProtocolBuilder httpProtocol = http
      .baseUrl(baseUrl)
      .acceptHeader("application/json")
      .contentTypeHeader("application/json");

  private Iterator<Map<String, Object>> randomFeeder = Stream.generate((Supplier<Map<String, Object>>) () -> {
    var randomId = String.valueOf(random.nextLong(1_000_000_000));
    var randomName = "User" + random.ints(97, 123)
        .limit(8)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    var randomEmail = "user" + random.nextInt(1_000_000) + "@example.com";

    return Collections.unmodifiableMap(Map.of(
        "randomId", randomId,
        "randomName", randomName,
        "randomEmail", randomEmail));
  }).iterator();

  {
    var config = GatlingConfig.load("create-users-simulation.conf");

    var percentCreate = config.getInt("percentCreate");
    var percentUpdate = config.getInt("percentUpdate");
    var percentRead = config.getInt("percentRead");

    var scn = scenario("Multiple POST Requests")
        .repeat(10_000).on(
            feed(randomFeeder).exec(
                randomSwitch()
                    .on(
                        percent(percentCreate).then(exec(
                            exec(session -> {
                              createdUserIds.add(session.getString("randomId"));
                              return session;
                            }).exec(
                                http("Create User")
                                    .post(path)
                                    .body(StringBody(
                                        "{\"userId\": \"#{randomId}\", \"name\": \"#{randomName}\", \"email\": \"#{randomEmail}\"}"))
                                    .check(status().is(200))
                                    .check(responseTimeInMillis().lte(5_000))))),
                        percent(percentUpdate).then(exec(
                            exec(session -> {
                              if (createdUserIds.isEmpty()) {
                                return session.set("existingId", "000000");
                              }
                              var existingId = createdUserIds.get(random.nextInt(createdUserIds.size()));
                              return session.set("existingId", existingId);
                            }).exec(
                                http("Update User Email")
                                    .put(path + "/change-email")
                                    .body(StringBody(
                                        "{\"userId\": \"#{existingId}\", \"email\": \"#{randomEmail}\"}"))
                                    .check(status().is(200))
                                    .check(responseTimeInMillis().lte(5_000))))),
                        percent(percentRead).then(exec( // percents should add up to 100
                            exec(session -> {
                              if (createdUserIds.isEmpty()) {
                                return session.set("existingId", "000000");
                              }
                              var existingId = createdUserIds.get(random.nextInt(createdUserIds.size()));
                              return session.set("existingId", existingId);
                            }).exec(
                                http("Read User")
                                    .get(path + "/#{existingId}")
                                    .check(status().is(200))
                                    .check(responseTimeInMillis().lte(2_000))))))
                    .pause(Duration.ofMillis(500))));

    {
      var reachRpsValue = config.getInt("reachRps");
      var holdFor = config.getDuration("holdFor");

      setUp(
          scn.injectOpen(
              rampUsers(1_000).during(Duration.ofMinutes(1))))
                  .throttle(
                      reachRps(reachRpsValue).in(Duration.ofMinutes(1)),
                      holdFor(holdFor),
                      reachRps(0).in(Duration.ofMinutes(1))) // sum the 3 durations for the total test run time
                  .protocols(httpProtocol);
    }
  }
}
