package com.onkibot.backend;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.exceptions.UserNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(OnkibotBackendApplication.class)
@WebAppConfiguration
@EnableJpaRepositories
@Sql(
  executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
  scripts = "classpath:./beforeTestRun.sql"
)
public class OnkibotBackendApplicationTest {
  @Autowired private UserRepository userRepository;

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private PasswordEncoder passwordEncoder;

  private MockHttpSession session;

  @Before
  public void setup() {
    this.session =
        new MockHttpSession(
            webApplicationContext.getServletContext(), UUID.randomUUID().toString());
  }

  @Test
  public void testMain() {
    OnkibotBackendApplication.main(new String[] {});
  }

  @Test
  public void testGetNoSessionUser() throws Exception {
    Optional<User> sessionUser =
        OnkibotBackendApplication.getSessionUser(userRepository, this.session);
    assertTrue(!sessionUser.isPresent());
  }

  @Test(expected = UserNotFoundException.class)
  public void testAssertNoSessionUser() throws Exception {
    OnkibotBackendApplication.assertSessionUser(userRepository, this.session);
  }

  @Test
  public void testSessionUser() throws Exception {
    User user = createUser("testPassword123");
    OnkibotBackendApplication.setSessionUser(user, this.session);

    Optional<User> sessionUser =
        OnkibotBackendApplication.getSessionUser(this.userRepository, this.session);
    assertTrue(sessionUser.isPresent());
    assertEquals(user.getUserId(), sessionUser.get().getUserId());

    OnkibotBackendApplication.setSessionUser(null, this.session);
    sessionUser = OnkibotBackendApplication.getSessionUser(this.userRepository, this.session);
    assertTrue(!sessionUser.isPresent());
  }

  @Test()
  public void testAssertSessionUser() throws Exception {
    User user = createUser("testPassword123");
    OnkibotBackendApplication.setSessionUser(user, this.session);

    OnkibotBackendApplication.assertSessionUser(userRepository, this.session);
  }

  private User createUser(String rawPassword) throws Exception {
    String encodedPassword = passwordEncoder.encode(rawPassword);
    User user = new User("test@onkibot.com", encodedPassword, "OnkiBOT Tester", true);
    return userRepository.save(user);
  }
}
