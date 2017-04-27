package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.exceptions.EmailInUseException;
import com.onkibot.backend.models.SignupInfoModel;
import com.onkibot.backend.models.UserDetailModel;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/** The SessionController controls the request done to the /signup API URL. */
@RestController
@RequestMapping(OnkibotBackendApplication.API_BASE_URL + "/signup")
public class SignupController {
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private AuthenticationManager authenticationManager;

  /**
   * This request requires a POST HTTP request to the /session API URL.
   *
   * <p>It attempts to sign-up the User.
   *
   * @param signupInfo The input model containing the signup information.
   * @param session The current session of the visitor.
   * @return The new {@link User} entity formatted through the {@link UserDetailModel}.
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<UserDetailModel> signup(
      @RequestBody SignupInfoModel signupInfo, HttpSession session) {
    // Encode the plaintext password through the PasswordEncoder service.
    String encodedPassword = passwordEncoder.encode(signupInfo.getPassword());
    // Check if the email is already in use.
    if (userRepository.findByEmail(signupInfo.getEmail()).isPresent()) {
      // The email is already in use, throw an exception that returns a Conflict status (409).
      throw new EmailInUseException(signupInfo.getEmail());
    }
    // Create the new User.
    User user =
        userRepository.save(
            new User(
                signupInfo.getEmail(),
                encodedPassword,
                signupInfo.getName(),
                signupInfo.getIsInstructor()));

    // Authenticate the new User and create a session for it.
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(signupInfo.getEmail(), signupInfo.getPassword());
    SecurityContextHolder.getContext()
        .setAuthentication(authenticationManager.authenticate(authentication));
    UserDetailModel userModel = new UserDetailModel(user);
    session.setAttribute("user", userModel);
    // Return the UserDetailModel of the User.
    return new ResponseEntity<>(userModel, HttpStatus.CREATED);
  }
}
