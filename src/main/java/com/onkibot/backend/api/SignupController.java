package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.exceptions.EmailInUseException;
import com.onkibot.backend.models.ResourceModel;
import com.onkibot.backend.models.SignupInfoModel;
import com.onkibot.backend.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(OnkibotBackendApplication.API_BASE_URL + "/signup")
public class SignupController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserModel> signup(
            @RequestBody SignupInfoModel signupInfo,
            HttpSession session
    ) {
        String encodedPassword = passwordEncoder.encode(signupInfo.getPassword());
        if (!userRepository.findByEmail(signupInfo.getEmail()).isPresent()) {
            User user = userRepository.save(new User(signupInfo.getEmail(), encodedPassword, signupInfo.getName(), signupInfo.getIsInstructor()));

            Authentication authentication = new UsernamePasswordAuthenticationToken(signupInfo.getEmail(), signupInfo.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authentication));
            UserModel userModel = new UserModel(user);
            session.setAttribute("user", userModel);
            return new ResponseEntity<>(userModel, HttpStatus.CREATED);
        } else {
            throw new EmailInUseException(signupInfo.getEmail());
        }
    }
}
