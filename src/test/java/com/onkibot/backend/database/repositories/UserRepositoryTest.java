package com.onkibot.backend.database.repositories;


import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(OnkibotBackendApplication.class)
@EnableJpaRepositories
@WebAppConfiguration
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:./beforeTestRun.sql")
public class UserRepositoryTest {
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void testCreateUser() {
        String plaintextPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(plaintextPassword);

        // Setup user
        User user = new User(
                "test@onkibot.com",
                encodedPassword,
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
        assertEquals(encodedPassword, user.getEncodedPassword());
        assertEquals(user.getEncodedPassword(), fetchedUser.getEncodedPassword());

        // Verify the user count in the db
        long userCount = userRepository.count();
        assertEquals(userCount, 1);

        // Get all the users from the database
        Iterable<User> products = userRepository.findAll();

        int count = 0;

        for(User u : products){
            count++;
        }

        assertEquals(count, 1);
    }
}
