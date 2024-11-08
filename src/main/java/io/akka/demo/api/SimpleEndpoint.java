package io.akka.demo.api;

import akka.Done;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import io.akka.demo.domain.User;

import java.net.InetAddress;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("/simple")
public class SimpleEndpoint {
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Post
  public CompletionStage<Done> createUser(CreateUserRequest request) {
    log.debug("{}", request);

    return CompletableFuture.completedFuture(Done.getInstance());
  }

  @Get("/{userId}")
  public CompletionStage<User.State> getUserInfo(String userId) {
    var hostname = "undefined hostname";
    try {
      hostname = InetAddress.getLocalHost().getHostName();
    } catch (java.net.UnknownHostException e) {
      log.error("Failed to get hostname", e);
    }
    return CompletableFuture
        .completedFuture(new User.State("user123", "User One", "user123@example.com", hostname, Instant.now()));
  }

  public record CreateUserRequest(String userId, String name, String email) {}
}
