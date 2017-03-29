package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.models.CredentialsModel;
import com.onkibot.backend.models.UserModel;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(OnkibotBackendApplication.API_BASE_URL + "/session")
public class SessionController {
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private UserRepository userRepository;

  @RequestMapping(method = RequestMethod.POST)
  public UserModel login(@RequestBody CredentialsModel credentials, HttpSession session) {
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
    SecurityContextHolder.getContext()
        .setAuthentication(authenticationManager.authenticate(authentication));
    User user = userRepository.findByEmail(credentials.getEmail()).get();
    OnkibotBackendApplication.setSessionUser(user, session);
    return new UserModel(user);
  }

  @RequestMapping(method = RequestMethod.GET)
  public UserModel session(HttpSession session) {
    return OnkibotBackendApplication.getSessionUser(userRepository, session)
        .map(UserModel::new)
        .orElse(null);
  }

  @RequestMapping(method = RequestMethod.DELETE)
  public void logout(HttpSession session) {
    session.invalidate();
  }
}
