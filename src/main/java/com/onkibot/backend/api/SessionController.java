package com.onkibot.backend.api;

import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.exceptions.UserNotFoundException;
import com.onkibot.backend.models.CredentialsModel;
import com.onkibot.backend.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/session")
public class SessionController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.POST)
    public UserModel login(
            @RequestBody CredentialsModel credentials,
            HttpSession session
    ) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authentication));
        UserModel userModel = new UserModel(userRepository.findByEmail(credentials.getEmail()).get());
        session.setAttribute("userId", userModel.getUserId());
        return userModel;
    }

    @RequestMapping(method = RequestMethod.GET)
    public UserModel session(HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        User user = userRepository.findByUserId().orElseThrow(() -> new UserNotFoundException(userId));
        return new UserModel(user);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
