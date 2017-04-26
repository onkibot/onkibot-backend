package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.exceptions.UserNotFoundException;
import com.onkibot.backend.models.UserModel;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/** The SessionController controls the request done to the /users API URL. */
@RestController
@RequestMapping(OnkibotBackendApplication.API_BASE_URL + "/users")
public class UserController {
  @Autowired private UserRepository userRepository;

  /**
   * This request requires a GET HTTP request to the /users/{userId} API URL.
   *
   * @param userId The {@link User} ID, this is handled by the PathVariable from Spring Boot.
   * @throws UserNotFoundException If a {@link User} with the <code>userId</code> is not found.
   * @return The {@link UserModel} of the requested <code>userId</code>.
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
  public UserModel get(@PathVariable int userId) {
    User user = User.assertUser(this.userRepository, userId);
    return new UserModel(user);
  }

  /**
   * This request requires a GET HTTP request to the /users API URL.
   *
   * @return A Collection of all the {@link User} entities formatted through the {@link UserModel}.
   */
  @RequestMapping(method = RequestMethod.GET)
  public List<UserModel> getAll() {
    ArrayList<UserModel> models = new ArrayList<>();
    userRepository.findAll().forEach(course -> models.add(new UserModel(course)));
    return models;
  }
}
