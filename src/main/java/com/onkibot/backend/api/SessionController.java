package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
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

@RestController
@RequestMapping(OnkibotBackendApplication.API_BASE_URL + "/session")
public class SessionController {
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private UserRepository userRepository;

  @RequestMapping(method = RequestMethod.POST)
  public UserDetailModel login(@RequestBody CredentialsModel credentials, HttpSession session) {
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
    SecurityContextHolder.getContext()
        .setAuthentication(authenticationManager.authenticate(authentication));
    User user = userRepository.findByEmail(credentials.getEmail()).get();
    OnkibotBackendApplication.setSessionUser(user, session);
    return new UserDetailModel(user);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<UserDetailModel> session(HttpSession session) {
    return OnkibotBackendApplication.getSessionUser(userRepository, session)
        .map(user -> new ResponseEntity<>(new UserDetailModel(user), HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @RequestMapping(method = RequestMethod.DELETE)
  public void logout(HttpSession session) {
    session.invalidate();
  }
}
