package com.onkibot.backend.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.models.UserModel;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
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
public class UserControllerTest {
  private static final String API_URL = OnkibotBackendApplication.API_BASE_URL + "/users";

  private MockMvc mockMvc;

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Before
  public void setup() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
            .apply(springSecurity())
            .build();
  }

  @Test
  public void testGetUserWithoutAuthentication() throws Exception {
    String rawPassword = "testPassword123";
    String encodedPassword = passwordEncoder.encode(rawPassword);

    // Create the user first
    User user = new User("test@onkibot.com", encodedPassword, "OnkiBOT Tester", true);
    userRepository.save(user);
    assertEquals(encodedPassword, user.getEncodedPassword());

    // Get the user from the URL
    this.mockMvc
        .perform(get(API_URL + "/" + user.getUserId()).accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetUserWithAuthentication() throws Exception {
    String rawPassword = "testPassword123";
    String encodedPassword = passwordEncoder.encode(rawPassword);

    // Create the user first
    User user = new User("test@onkibot.com", encodedPassword, "OnkiBOT Tester", true);
    userRepository.save(user);
    assertEquals(encodedPassword, user.getEncodedPassword());

    // Get the user from the URL
    MvcResult result =
        this.mockMvc
            .perform(get(API_URL + "/" + user.getUserId()).accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andReturn();

    String jsonString = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();

    assertResponseModel(user, mapper.readValue(jsonString, UserModel.class));
  }

  @Test
  public void testGetNonExistingUserWithoutAuthentication() throws Exception {
    this.mockMvc
        .perform(get(API_URL + "/2").accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetNonExistingUserWithAuthentication() throws Exception {
    this.mockMvc
        .perform(get(API_URL + "/2").accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isNotFound());
  }

  private void assertResponseModel(User user, UserModel responseModel) throws IOException {
    assertEquals(1, responseModel.getUserId());
    assertEquals(user.getName(), responseModel.getName());
    assertEquals(user.getIsInstructor(), responseModel.getIsInstructor());
  }
}
