package io.akka.demo.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.Done;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.ComponentId;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import io.akka.demo.domain.User;

@ComponentId("user")
@Acl(allow = @Acl.Matcher(service = "*"))
public class UserEntity extends EventSourcedEntity<User.State, User.Event> {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final String entityId;

  public UserEntity(EventSourcedEntityContext context) {
    entityId = context.entityId();
  }

  public Effect<Done> createUser(User.Command.CreateUser command) {
    log.info("EntityId: {}\n_State: {}\n_Command: {}", entityId, currentState(), command);

    // Validate the command
    if (isBlank(command.name()) || isBlank(command.email())) {
      return effects().error("Name and email are required");
    }

    var event = currentState().onCommand(command);
    if (event.isEmpty()) {
      return effects().reply(Done.getInstance()); // User already exists
    } else {
      return effects()
          .persist(event.get())
          .thenReply(__ -> Done.getInstance());
    }
  }

  public Effect<Done> changeEmail(User.Command.ChangeEmail command) {
    log.info("EntityId: {}\n_State: {}\n_Command: {}", entityId, currentState(), command);

    // Validate the command
    if (isBlank(command.email())) {
      return effects().error("Email is required");
    }

    var event = currentState().onCommand(command);
    if (event.isEmpty()) {
      return effects().reply(Done.getInstance()); // Email already changed
    } else {
      return effects()
          .persist(event.get())
          .thenReply(__ -> Done.getInstance());
    }
  }

  public ReadOnlyEffect<User.State> get() {
    if (currentState().isEmpty()) {
      return effects().error("User '%s' does not exist".formatted(entityId));
    }
    return effects().reply(currentState());
  }

  @Override
  public User.State emptyState() {
    return User.State.empty();
  }

  @Override
  public User.State applyEvent(User.Event event) {
    return switch (event) {
      case User.Event.UserCreated userCreated -> currentState().onEvent(userCreated);
      case User.Event.EmailChanged emailChanged -> currentState().onEvent(emailChanged);
      default -> currentState();
    };
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
