package com.onkibot.backend.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.models.SignupInfoModel;
import com.onkibot.backend.models.UserDetailModel;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(OnkibotBackendApplication.class)
@WebAppConfiguration
@Sql(
  executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
  scripts = "classpath:./beforeTestRun.sql"
)
public class SignupControllerTest {
  private static final String API_URL = OnkibotBackendApplication.API_BASE_URL + "/signup";

  private MockMvc mockMvc;
  private MockHttpSession mockHttpSession;

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Before
  public void setup() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
            .apply(springSecurity())
            .build();
    this.resetSession();
  }

  // TODO: test session

  @Test
  public void testSignupUser() throws Exception {
    this.resetSession();
    String rawPassword = "testPassword123";
    ObjectMapper mapper = new ObjectMapper();

    // Create the user first
    SignupInfoModel signupInfoModel =
        new SignupInfoModel("test@onkibot.com", rawPassword, "OnkiBOT Tester", "instructor");

    MvcResult signupResult =
        this.mockMvc
            .perform(
                post(API_URL)
                    .session(this.mockHttpSession)
                    .content(mapper.writeValueAsString(signupInfoModel))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andReturn();

    String jsonString = signupResult.getResponse().getContentAsString();

    UserDetailModel responseUserModel = mapper.readValue(jsonString, UserDetailModel.class);

    assertEquals(1, responseUserModel.getUserId());
    assertEquals(signupInfoModel.getEmail(), responseUserModel.getEmail());
    assertEquals(signupInfoModel.getName(), responseUserModel.getName());
    assertEquals(signupInfoModel.getIsInstructor(), responseUserModel.getIsInstructor());
    assertTrue(responseUserModel.getAttending().isEmpty());
    assertTrue(responseUserModel.getResources().isEmpty());
    assertTrue(responseUserModel.getExternalResources().isEmpty());
  }

  @Test
  public void testSignupExistingUserEmail() throws Exception {
    this.resetSession();
    String rawPassword = "testPassword123";
    String encodedPassword = passwordEncoder.encode(rawPassword);

    // Create the user first
    User user = new User("test@onkibot.com", encodedPassword, "OnkiBOT Tester", true);
    userRepository.save(user);

    // Create the user first
    ObjectMapper mapper = new ObjectMapper();
    SignupInfoModel signupInfoModel =
        new SignupInfoModel("test@onkibot.com", rawPassword, "OnkiBOT Tester", "instructor");
    this.mockMvc
        .perform(
            post(API_URL)
                .session(this.mockHttpSession)
                .content(mapper.writeValueAsString(signupInfoModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
        .andExpect(status().isConflict());
  }

  private void resetSession() {
    this.mockHttpSession =
        new MockHttpSession(
            webApplicationContext.getServletContext(), UUID.randomUUID().toString());
  }
}
