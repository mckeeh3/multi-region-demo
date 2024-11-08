package io.akka.demo.domain;

import java.net.InetAddress;
import java.time.Instant;
import java.util.Optional;

public interface User {
  public record State(String userId, String name, String email, String hostname, Instant timeOfLastUpdate) {

    public Optional<Event> onCommand(Command.CreateUser command) {
      if (this.isEmpty()) { // User not created yet
        var hostname = "unknown hostname";
        try {
          hostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
          // Ignore
        }
        return Optional
            .of(new Event.UserCreated(command.userId(), command.name(), command.email(), hostname, Instant.now()));
      } else {
        return Optional.empty(); // User already exists
      }
    }

    public Optional<Event> onCommand(Command.ChangeEmail command) {
      if (command.email().equals(this.email)) {
        return Optional.empty(); // Email already changed
      } else {
        return Optional.of(new Event.EmailChanged(command.userId(), command.email(), Instant.now()));
      }
    }

    public State onEvent(Event.UserCreated event) {
      return new State(event.userId(), event.name(), event.email(), event.hostname, event.timeCreated());
    }

    public State onEvent(Event.EmailChanged event) {
      return new State(userId, name, event.email(), hostname, event.timeUpdated());
    }

    public static State empty() {
      return new State(null, null, null, null, null);
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
    record UserCreated(String userId, String name, String email, String hostname, Instant timeCreated)
        implements Event {}

    record EmailChanged(String userId, String email, Instant timeUpdated) implements Event {}
  }
}
