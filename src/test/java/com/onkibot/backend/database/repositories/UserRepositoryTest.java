package com.onkibot.backend.database.repositories;


import com.onkibot.backend.configuration.RepositoryConfiguration;
import com.onkibot.backend.database.entities.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(RepositoryConfiguration.class)
@EnableJpaRepositories
@WebAppConfiguration
public class UserRepositoryTest {
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void testCreateUser() {
        // Setup user
        User user = new User(
                "test@onkibot.com",
                "da",
                "OnkiBOT Tester",
                true
        );

        // Save/Create user, verify has ID value after save
        assertNull(user.getUserId()); // Null before save
        userRepository.save(user);
        assertNotNull(user.getUserId()); // Not null after save

        // Fetch the user from DB
        User fetchedUser = userRepository.findOne(user.getUserId());

        // The fetched user should not be null
        assertNotNull(fetchedUser);

        // The values of the users should be equal
        assertEquals(user.getUserId(), fetchedUser.getUserId());
        assertEquals(user.getEmail(), fetchedUser.getEmail());
        assertEquals(user.getName(), fetchedUser.getName());
        assertEquals(user.getEncodedPassword(), fetchedUser.getEncodedPassword());
    }
}
