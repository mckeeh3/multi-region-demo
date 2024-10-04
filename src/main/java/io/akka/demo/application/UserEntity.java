package io.akka.demo.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.Done;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.ComponentId;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import io.akka.demo.domain.User;
import static akka.Done.done;

@ComponentId("user")
@Acl(allow = @Acl.Matcher(service = "*"))
public class UserEntity extends EventSourcedEntity<User.State, User.Event> {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final String entityId;

  public UserEntity(EventSourcedEntityContext context) {
    entityId = context.entityId();
  }

  public Effect<Done> createUser(User.Command.CreateUser command) {
    log.debug("EntityId: {}\n_State: {}\n_Command: {}", entityId, currentState(), command);

    if (!currentState().equals(emptyState())) {
      return effects().reply(done()); // Already exists
    }

    if (command.name() == null || command.email() == null) {
      return effects().error("Name and email are required");
    }

    log.info("Creating user: {}", command);
    return effects()
        .persist(currentState().onCommand(command))
        .thenReply(__ -> done());
  }

  public ReadOnlyEffect<User.State> get() {
    if (currentState().equals(emptyState())) {
      return effects().error("User '%s' does not exist".formatted(entityId));
    }
    return effects().reply(currentState());
  }

  @Override
  public User.State emptyState() {
    return new User.State(null, null, null);
  }

  @Override
  public User.State applyEvent(User.Event event) {
    return switch (event) {
      case User.Event.UserCreated userCreated -> currentState().onEvent(userCreated);
    };
  }
}
