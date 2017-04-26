package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.models.CredentialsModel;
import com.onkibot.backend.models.UserDetailModel;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * The SessionController controls the request done to the /session API URL.
 */
@RestController
@RequestMapping(OnkibotBackendApplication.API_BASE_URL + "/session")
public class SessionController {
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private UserRepository userRepository;

  /**
   * This request requires a POST HTTP request to the /session API URL.
   *
   * @param credentials The credentials (email & password) for the User.
   * @param session The current session of the visitor.
   * @return The User entity formatted through the UserDetailModel.
   */
  @RequestMapping(method = RequestMethod.POST)
  public UserDetailModel login(@RequestBody CredentialsModel credentials, HttpSession session) {
    // Get the authentication token through the Authentication service.
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
    // Authenticate the User.
    SecurityContextHolder.getContext()
        .setAuthentication(authenticationManager.authenticate(authentication));
    // Set the User of the session and return the UserDetailModel of the User.
    User user = userRepository.findByEmail(credentials.getEmail()).get();
    OnkibotBackendApplication.setSessionUser(user, session);
    return new UserDetailModel(user);
  }

  /**
   * This request requires a GET HTTP request to the /session API URL.
   *
   * @param session The current session of the visitor.
   * @return The User entity formatted through the {@link UserDetailModel} or NULL if the user is not logged in.
   */
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<UserDetailModel> session(HttpSession session) {
    return OnkibotBackendApplication.getSessionUser(userRepository, session)
        .map(user -> new ResponseEntity<>(new UserDetailModel(user), HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * This request requires a DELETE HTTP request to the /session API URL.
   * <p>
   * It logs the user out.
   *
   * @param session The current session of the visitor that we wish to logout.
   */
  @RequestMapping(method = RequestMethod.DELETE)
  public void logout(HttpSession session) {
    session.invalidate();
  }
}
