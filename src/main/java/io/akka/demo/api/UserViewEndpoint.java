package io.akka.demo.api;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import akka.http.javadsl.model.ContentType;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.model.headers.CacheControl;
import akka.http.javadsl.model.headers.CacheDirectives;
import akka.http.javadsl.model.headers.Connection;
import akka.javasdk.JsonSupport;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.client.ComponentClient;
import akka.util.ByteString;
import io.akka.demo.application.UserView;

@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("/user-view")
public class UserViewEndpoint {
  private ComponentClient componentClient;

  public UserViewEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Get("/by-id/{userId}")
  public CompletionStage<UserView.UserRow> getUserInfo(String userId) {
    return componentClient.forView()
        .method(UserView::getUserById)
        .invokeAsync(userId);
  }

  @Get("/by-email/{email}")
  public CompletionStage<UserView.UserRow> getUserByEmail(String email) {
    return componentClient.forView()
        .method(UserView::getUserByEmail)
        .invokeAsync(email);
  }

  private static final ContentType contentType = ContentTypes.parse("text/event-stream");
  private static final ByteString prefix = ByteString.fromString("data: ");
  private static final ByteString suffix = ByteString.fromString("\n\n");

  // See server sent events
  // https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#event_stream_format
  @Get("/all")
  public HttpResponse getAllUsers() {
    var source = componentClient.forView()
        .stream(UserView::getAllUsers)
        .source();

    var stream = source.map(row -> prefix.concat(JsonSupport.encodeToAkkaByteString(row)).concat(suffix));

    return HttpResponse.create()
        .withStatus(StatusCodes.OK)
        .withHeaders(Arrays.asList(
            CacheControl.create(CacheDirectives.NO_CACHE),
            Connection.create("keep-alive")))
        .withEntity(HttpEntities.create(contentType, stream));
  }
}
