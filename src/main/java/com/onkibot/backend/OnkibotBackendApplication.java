package com.onkibot.backend;

import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.exceptions.UserNotFoundException;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of this project.
 */
@SpringBootApplication
public class OnkibotBackendApplication {
  public static final String API_BASE_URL = "/api/v1";

  /**
   * Returns the {@link User} of the current {@link HttpSession}.
   *
   * @param userRepository The Repository service for the {@link User} entity.
   * @param session The {@link HttpSession} we want to get the {@link User} from.
   * @return The {@link User} of the <code>session</code> or {@link Optional#empty()} if the session is not set.
   */
  public static Optional<User> getSessionUser(UserRepository userRepository, HttpSession session) {
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId != null) {
      return userRepository.findByUserId(userId);
    } else {
      return Optional.empty();
    }
  }

  /**
   *
   * @param userRepository The Repository service for the {@link User} entity.
   * @param session The {@link HttpSession} we want to assert.
   * @throws UserNotFoundException If the <code>userId</code> in the <code>session</code> is not set,
   *                               or {@link User} from the <code>session</code> is not found.
   * @return The {@link User} of the <code>session</code>.
   */
  public static User assertSessionUser(UserRepository userRepository, HttpSession session) {
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId != null) {
      return userRepository
          .findByUserId(userId)
          .orElseThrow(() -> new UserNotFoundException(userId));
    } else {
      throw new UserNotFoundException();
    }
  }

  /**
   * Sets the <code>userId</code> in the {@link HttpSession} <code>session</code>.
   *
   * @param user The user, this can either be a valid {@link User} entity, or <code>null</code>.
   * @param session The {@link HttpSession} we want to set the <code>user</code> for.
   */
  public static void setSessionUser(User user, HttpSession session) {
    if (user != null) {
      session.setAttribute("userId", user.getUserId());
    } else {
      session.setAttribute("userId", null);
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(OnkibotBackendApplication.class, args);
  }
}
