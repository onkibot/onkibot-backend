package com.onkibot.backend.api;

import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.models.SignupInfoModel;
import com.onkibot.backend.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/signup")
public class SignupController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping(method = RequestMethod.POST)
    public UserModel signup(
            @RequestBody SignupInfoModel signupInfo,
            HttpSession session
    ) {
        // Always encode the password to introduce delay
        String encodedPassword = passwordEncoder.encode(signupInfo.getPassword());
        if (userRepository.findByEmail(signupInfo.getEmail()) == null) {
            User user = userRepository.save(new User(signupInfo.getEmail(), encodedPassword, signupInfo.getName(), signupInfo.getIsInstructor()));

            Authentication authentication = new UsernamePasswordAuthenticationToken(signupInfo.getEmail(), signupInfo.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authentication));
            UserModel userModel = new UserModel(user);
            session.setAttribute("user", userModel);
            return userModel;
        } else {
            return null;
        }
    }
}
