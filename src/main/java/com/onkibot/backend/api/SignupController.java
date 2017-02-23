package com.onkibot.backend.api;

import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/signup")
public class SignupController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping(method = RequestMethod.POST)
    public UserModel signup(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String name,
            @RequestParam boolean isInstructor,
            HttpSession session
    ) {
        // Always encode the password to introduce delay
        String encodedPassword = passwordEncoder.encode(password);
        if (userRepository.findByEmail(email) == null) {
            User user = userRepository.save(new User(email, encodedPassword, name, isInstructor));

            Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
            SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authentication));
            UserModel userModel = new UserModel(user);
            session.setAttribute("user", userModel);
            return userModel;
        } else {
            return null;
        }
    }
}
