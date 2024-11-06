package io.akka.demo.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.javasdk.annotations.ComponentId;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import io.akka.demo.domain.User;

@ComponentId("user-view")
public class UserView extends View {
  @Query("""
      SELECT *
        FROM user_view
       WHERE id = :id
      """)
  public QueryEffect<UserRow> getUserById(String id) {
    return queryResult();
  }

  @Query("""
      SELECT *
        FROM user_view
       WHERE email = :email
      """)
  public QueryEffect<UserRow> getUserByEmail(String email) {
    return queryResult();
  }

  @Query(value = """
      SELECT *
        FROM user_view
      """, streamUpdates = true)
  public QueryStreamEffect<UserRow> getAllUsers() {
    return queryStreamResult();
  }

  public record UserRow(String id, String name, String email) {
    public UserRow withEmail(String newEmail) {
      return new UserRow(id, name, newEmail);
    }
  }

  @Consume.FromEventSourcedEntity(UserEntity.class)
  public static class UserRowUpdater extends TableUpdater<UserRow> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Effect<UserRow> onEvent(User.Event event) {
      log.info("RowId: {}\n_RowState: {}\n_Event: {}", rowState() == null ? "N/A" : rowState().id(), rowState(), event);

      return switch (event) {
        case User.Event.UserCreated userCreated -> effects()
            .updateRow(new UserRow(userCreated.userId(), userCreated.name(), userCreated.email()));
        case User.Event.EmailChanged emailChanged -> effects().updateRow(rowState().withEmail(emailChanged.email()));
      };
    }
  }
}
