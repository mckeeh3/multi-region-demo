package io.akka.demo.domain;

import java.util.Optional;

public interface User {
  public record State(String userId, String name, String email) {
    public Optional<Event> onCommand(Command.CreateUser command) {
      if (this.isEmpty()) {
        return Optional.of(new Event.UserCreated(command.userId(), command.name(), command.email()));
      } else {
        return Optional.empty(); // Already exists
      }
    }

    public Optional<Event> onCommand(Command.ChangeEmail command) {
      if (command.email().equals(this.email)) {
        return Optional.empty(); // Email already changed
      } else {
        return Optional.of(new Event.EmailChanged(command.userId(), command.email()));
      }
    }

    public State onEvent(Event.UserCreated event) {
      return new State(event.userId(), event.name(), event.email());
    }

    public State onEvent(Event.EmailChanged event) {
      return new State(userId, name, event.email());
    }

    public static State empty() {
      return new State(null, null, null);
    }

    public boolean isEmpty() {
      return this.equals(empty());
    }
  }

  public sealed interface Command {
    record CreateUser(String userId, String name, String email) implements Command {}

    record ChangeEmail(String userId, String email) implements Command {}
  }

  public sealed interface Event {
    record UserCreated(String userId, String name, String email) implements Event {}

    record EmailChanged(String userId, String email) implements Event {}
  }
}
