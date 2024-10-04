package io.akka.demo.domain;

import akka.javasdk.annotations.TypeName;

public interface User {
  public record State(String userId, String name, String email) {
    public Event onCommand(Command.CreateUser command) {
      return new Event.UserCreated(command.userId(), command.name(), command.email());
    }

    public State onEvent(Event.UserCreated event) {
      return new State(event.userId(), event.name(), event.email());
    }
  }

  sealed interface Command {
    record CreateUser(String userId, String name, String email) implements Command {}
  }

  sealed interface Event {
    @TypeName("UserCreated")
    record UserCreated(String userId, String name, String email) implements Event {}
  }
}
