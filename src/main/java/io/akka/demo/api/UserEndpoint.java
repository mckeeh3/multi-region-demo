package io.akka.demo.api;

import akka.Done;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.annotations.http.Put;
import akka.javasdk.client.ComponentClient;
import io.akka.demo.application.UserEntity;
import io.akka.demo.domain.User;

import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("/user")
public class UserEndpoint {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private ComponentClient entityClient;

  public UserEndpoint(ComponentClient client) {
    this.entityClient = client;
  }

  @Post()
  public CompletionStage<Done> createUser(CreateUserRequest request) {
    log.debug("{}", request);
    var command = new User.Command.CreateUser(request.userId(), request.name(), request.email());

    return entityClient.forEventSourcedEntity(request.userId())
        .method(UserEntity::createUser)
        .invokeAsync(command);
  }

  @Put("/change-email")
  public CompletionStage<Done> updateEmail(ChangeEmailRequest request) {
    log.debug("{}", request);
    var command = new User.Command.ChangeEmail(request.userId(), request.email());

    return entityClient.forEventSourcedEntity(request.userId())
        .method(UserEntity::changeEmail)
        .invokeAsync(command);
  }

  @Get("/{userId}")
  public CompletionStage<User.State> getUserInfo(String userId) {
    return entityClient.forEventSourcedEntity(userId)
        .method(UserEntity::get)
        .invokeAsync();
  }

  public record CreateUserRequest(String userId, String name, String email) {}

  public record ChangeEmailRequest(String userId, String email) {}
}
