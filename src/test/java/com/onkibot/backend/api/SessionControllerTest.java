package com.onkibot.backend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.models.CredentialsModel;
import com.onkibot.backend.models.UserModel;
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

import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(OnkibotBackendApplication.class)
@WebAppConfiguration
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:./beforeTestRun.sql")
public class SessionControllerTest {
    private final static String API_URL = OnkibotBackendApplication.API_BASE_URL + "/session";
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
        this.resetSession();
    }

    @Test
    public void testLoginUser() throws Exception {
        this.resetSession();
        String rawPassword = "testPassword123";
        User user = createUser(rawPassword);
        MvcResult loginResult = loginUser(user, rawPassword);

        Integer userId = (Integer) this.mockHttpSession.getAttribute("userId");
        assertNotNull(userId);
        assertEquals(user.getUserId(), userId);

        assertFalse(this.mockHttpSession.isInvalid());

        String jsonString = loginResult.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        UserModel responseUserModel = mapper.readValue(jsonString, UserModel.class);
        assertEquals(1, responseUserModel.getUserId());
        assertEquals(user.getEmail(), responseUserModel.getEmail());
        assertEquals(user.getName(), responseUserModel.getName());
        assertEquals(user.getIsInstructor(), responseUserModel.getIsInstructor());

        // Get the current session
        MvcResult sessionResult = this.mockMvc.perform(get(API_URL)
                .session(this.mockHttpSession)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String sessionJsonString = sessionResult.getResponse().getContentAsString();
        assertEquals(jsonString, sessionJsonString);
    }

    @Test
    public void testLogoutUser() throws Exception {
        this.resetSession();
        String rawPassword = "testPassword123";
        User user = createUser(rawPassword);

        loginUser(user, rawPassword);

        this.mockMvc.perform(delete(API_URL)
                .session(this.mockHttpSession)
                .accept(MediaType.ALL))
                .andExpect(status().isOk());

        assertTrue(this.mockHttpSession.isInvalid());

        // Get the current session
        MvcResult sessionResult = this.mockMvc.perform(get(API_URL)
                .session(this.mockHttpSession)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String jsonString = sessionResult.getResponse().getContentAsString();
        assertEquals("", jsonString);
    }

    private User createUser(String rawPassword) throws Exception {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        // Create the user first
        User user = new User(
                "test@onkibot.com",
                encodedPassword,
                "OnkiBOT Tester",
                true
        );
        userRepository.save(user);

        assertEquals(encodedPassword, user.getEncodedPassword());

        return user;
    }

    private MvcResult loginUser(User user, String rawPassword) throws Exception {
        // Create the object request
        ObjectMapper mapper = new ObjectMapper();
        CredentialsModel credentialsModel = new CredentialsModel(
                user.getEmail(),
                rawPassword
        );

        // Return the results
        return this.mockMvc.perform(post(API_URL)
                .content(mapper.writeValueAsString(credentialsModel))
                .session(this.mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
    }

    private void resetSession() {
        this.mockHttpSession = new MockHttpSession(
                webApplicationContext.getServletContext(),
                UUID.randomUUID().toString()
        );
    }
}
