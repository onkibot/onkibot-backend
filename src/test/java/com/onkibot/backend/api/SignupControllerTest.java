package com.onkibot.backend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.models.SignupInfoModel;
import com.onkibot.backend.models.UserModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(OnkibotBackendApplication.class)
@WebAppConfiguration
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:./beforeTestRun.sql")
public class SignupControllerTest {
    private MockMvc mockMvc;
    private final static String API_URL = OnkibotBackendApplication.API_BASE_URL + "/signup";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setup() {
        this.mockMvc = (MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build());
    }

    @Test
    public void testSignupUser() throws Exception {
        // Create the user first
        ObjectMapper mapper = new ObjectMapper();
        SignupInfoModel signupInfoModel = new SignupInfoModel(
                "test@onkibot.com",
                "testPassword",
                "OnkiBOT Tester",
                true
        );
        MvcResult signupResult = this.mockMvc.perform(post(API_URL)
                .content(mapper.writeValueAsString(signupInfoModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonString = signupResult.getResponse().getContentAsString();

        UserModel responseUserModel = mapper.readValue(jsonString, UserModel.class);
        System.out.println(responseUserModel.getName());
    }
}
