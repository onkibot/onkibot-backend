package com.onkibot.backend.database.repositories;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.models.CourseModel;
import com.onkibot.backend.models.CredentialsModel;
import com.onkibot.backend.models.UserModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(OnkibotBackendApplication.class)
@EnableJpaRepositories
@WebAppConfiguration
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:./beforeTestRun.sql")
public class CourseControllerTest {
    private final static String API_URL_COURSE = OnkibotBackendApplication.API_BASE_URL + "/courses";
    private final static String API_URL_SESSION = OnkibotBackendApplication.API_BASE_URL + "/session";
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setup() {
        this.mockMvc = (MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build());
        this.resetSession();
    }

    // TODO: fix @Test
    public void testGetCourseWithoutAuthentication() throws Exception {
        this.resetSession();

        Course course = createRepositoryCourse();

        this.mockMvc.perform(get(API_URL_COURSE + "/" + course.getCourseId())
                .session(this.mockHttpSession)
                .accept(MediaType.ALL))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetCourseWithAuthentication() throws Exception {
        this.resetSession();

        String rawPassword = "testPassword123";
        Course course = createRepositoryCourse();
        User user = createRepositoryUser(rawPassword);
        loginUser(user, rawPassword);

        MvcResult result = this.mockMvc.perform(get(API_URL_COURSE + "/" + course.getCourseId())
                .session(this.mockHttpSession)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String jsonString = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        CourseModel responseCourseModel = mapper.readValue(jsonString, CourseModel.class);

        assertEquals(1, responseCourseModel.getCourseId());
        assertEquals(course.getName(), responseCourseModel.getName());
        assertEquals(course.getDescription(), responseCourseModel.getDescription());
        assertEquals(course.getCategories().size(), responseCourseModel.getCategories().size());
    }

    private Course createRepositoryCourse() {
        // Setup course
        Course course = new Course(
                "Test course",
                "Test course description"
        );
        courseRepository.save(course);
        return course;
    }

    private User createRepositoryUser(String rawPassword) {
        // Setup user
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(
                "test@onkibot.com",
                encodedPassword,
                "OnkiBOT Tester",
                true
        );
        userRepository.save(user);
        return user;
    }

    private void loginUser(User user, String rawPassword) throws Exception {
        // Create the object request
        ObjectMapper mapper = new ObjectMapper();
        CredentialsModel credentialsModel = new CredentialsModel(
                user.getEmail(),
                rawPassword
        );

        // Post the session
        this.mockMvc.perform(post(API_URL_SESSION)
                .content(mapper.writeValueAsString(credentialsModel))
                .session(this.mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    private void resetSession() {
        this.mockHttpSession = new MockHttpSession(
                webApplicationContext.getServletContext(),
                UUID.randomUUID().toString()
        );
    }
}
