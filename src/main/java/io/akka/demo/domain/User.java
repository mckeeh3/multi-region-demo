package io.akka.demo.domain;

public interface User {
  public record State(String userId, String name, String email) {
    public Event onCommand(Command.CreateUser command) {
      // do some business logic
      return new Event.UserCreated(command.userId(), command.name(), command.email());
    }

    public State onEvent(Event.UserCreated event) {
      return new State(event.userId(), event.name(), event.email());
    }
  }

  public sealed interface Command {
    record CreateUser(String userId, String name, String email) implements Command {}
  }

  public sealed interface Event {
    record UserCreated(String userId, String name, String email) implements Event {}
  }
}
