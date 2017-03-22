package com.onkibot.backend.api;

import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.exceptions.UserNotFoundException;
import com.onkibot.backend.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(OnkibotBackendApplication.API_BASE_URL + "/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    public UserModel get(@PathVariable int userId) {
        User user = assertUser(userId);
        return new UserModel(user);
    }

    private User assertUser(int userId) {
        return this.userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}
