package com.onkibot.backend;

import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.exceptions.UserNotFoundException;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnkibotBackendApplication {
  public static final String API_BASE_URL = "/api/v1";

  public static Optional<User> getSessionUser(UserRepository userRepository, HttpSession session) {
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId != null) {
      return userRepository.findByUserId(userId);
    } else {
      return Optional.empty();
    }
  }

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
