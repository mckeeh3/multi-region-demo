package io.akka.demo.api;

import java.util.concurrent.CompletionStage;

import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.client.ComponentClient;
import io.akka.demo.application.UserView;

@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("/user-view")
public class UserViewEndpoint {
  private ComponentClient entityClient;

  public UserViewEndpoint(ComponentClient client) {
    this.entityClient = client;
  }

  @Get("/by-id/{userId}")
  public CompletionStage<UserView.UserRow> getUserInfo(String userId) {
    return entityClient.forView()
        .method(UserView::getUserById)
        .invokeAsync(userId);
  }

  @Get("/by-email/{email}")
  public CompletionStage<UserView.UserRow> getUserByEmail(String email) {
    return entityClient.forView()
        .method(UserView::getUserByEmail)
        .invokeAsync(email);
  }
}
