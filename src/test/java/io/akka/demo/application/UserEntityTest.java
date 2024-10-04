package io.akka.demo.application;

import static akka.Done.done;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import akka.javasdk.testkit.EventSourcedTestKit;
import io.akka.demo.domain.User;

public class UserEntityTest {
  @Test
  void testGetForNonexistentUser() {
    var testKit = EventSourcedTestKit.of(UserEntity::new);

    var result = testKit.call(entity -> entity.get());

    assertTrue(result.isError());
  }

  @Test
  public void testCreateUser() {
    var testKit = EventSourcedTestKit.of(UserEntity::new);

    var userId = "123";
    var name = "John Doe";
    var email = "john.doe@example.com";

    {
      var command = new User.Command.CreateUser(userId, name, email);
      var result = testKit.call(entity -> entity.createUser(command));

      assertTrue(result.isReply());
      assertEquals(done(), result.getReply());

      var event = result.getNextEventOfType(User.Event.UserCreated.class);

      assertEquals(userId, event.userId());
      assertEquals(name, event.name());
      assertEquals(email, event.email());

      var state = testKit.getState();
      assertEquals(userId, state.userId());
      assertEquals(name, state.name());
      assertEquals(email, state.email());
    }
  }

  @Test
  void testGetForExistingUser() {
    var testKit = EventSourcedTestKit.of(UserEntity::new);

    var userId = "123";
    var name = "John Doe";
    var email = "john.doe@example.com";

    {
      var command = new User.Command.CreateUser(userId, name, email);
      var result = testKit.call(entity -> entity.createUser(command));

      assertTrue(result.isReply());
      assertEquals(done(), result.getReply());
    }

    {
      var result = testKit.call(entity -> entity.get());

      assertTrue(result.isReply());
      assertEquals(userId, result.getReply().userId());
      assertEquals(name, result.getReply().name());
      assertEquals(email, result.getReply().email());
    }
  }
}
