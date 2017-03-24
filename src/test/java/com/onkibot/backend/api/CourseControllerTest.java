package com.onkibot.backend.api;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.database.repositories.UserRepository;
import com.onkibot.backend.models.*;
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

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
        this.resetSession();
    }

    @Test
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

        Course course = createRepositoryCourse();
        String rawPassword = "testPassword123";
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

        assertEquals(course.getCourseId(), responseCourseModel.getCourseId());
        assertEquals(course.getName(), responseCourseModel.getName());
        assertEquals(course.getDescription(), responseCourseModel.getDescription());
        assertEquals(course.getCategories().size(), responseCourseModel.getCategories().size());
    }

    @Test
    public void testGetCoursesWithoutAuthentication() throws Exception {
        this.resetSession();

        this.mockMvc.perform(get(API_URL_COURSE)
                .session(this.mockHttpSession)
                .accept(MediaType.ALL))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetCoursesWithAuthentication() throws Exception {
        this.resetSession();

        Course course1 = createRepositoryCourse();
        Course course2 = createRepositoryCourse();
        String rawPassword = "testPassword123";
        User user = createRepositoryUser(rawPassword);
        loginUser(user, rawPassword);

        MvcResult result = this.mockMvc.perform(get(API_URL_COURSE)
                .session(this.mockHttpSession)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String jsonString = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<CourseModel> responseCourses = mapper.readValue(
                jsonString,
                new TypeReference<List<CourseModel>>(){}
        );

        assertEquals(responseCourses.size(), 2);

        CourseModel responseCourseModel1 = responseCourses.get(0);
        CourseModel responseCourseModel2 = responseCourses.get(1);


        assertEquals(course1.getCourseId(), responseCourseModel1.getCourseId());
        assertEquals(course1.getName(), responseCourseModel1.getName());
        assertEquals(course1.getDescription(), responseCourseModel1.getDescription());
        assertEquals(course1.getCategories().size(), responseCourseModel1.getCategories().size());


        assertEquals(course2.getCourseId(), responseCourseModel2.getCourseId());
        assertEquals(course2.getName(), responseCourseModel2.getName());
        assertEquals(course2.getDescription(), responseCourseModel2.getDescription());
        assertEquals(course2.getCategories().size(), responseCourseModel2.getCategories().size());
    }

    @Test
    public void testCreateCourseWithoutAuthentication() throws Exception {
        this.resetSession();
        ObjectMapper mapper = new ObjectMapper();

        CourseInputModel courseInputModel = new CourseInputModel(
                "Random course",
                "Random description"
        );


       this.mockMvc.perform(post(API_URL_COURSE)
                .session(this.mockHttpSession)
                .content(mapper.writeValueAsString(courseInputModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateCourseWithAuthentication() throws Exception {
        this.resetSession();
        ObjectMapper mapper = new ObjectMapper();

        String rawPassword = "testPassword123";
        User user = createRepositoryUser(rawPassword);
        loginUser(user, rawPassword);

        CourseInputModel courseInputModel = new CourseInputModel(
                "Random course",
                "Random description"
        );


        MvcResult courseCreationResult = this.mockMvc.perform(post(API_URL_COURSE)
                .session(this.mockHttpSession)
                .content(mapper.writeValueAsString(courseInputModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonString = courseCreationResult.getResponse().getContentAsString();

        CourseModel responseCourseModel = mapper.readValue(jsonString, CourseModel.class);
        assertEquals(1, (int) responseCourseModel.getCourseId());
        assertEquals(courseInputModel.getName(), responseCourseModel.getName());
        assertEquals(courseInputModel.getDescription(), responseCourseModel.getDescription());
        assertEquals(0, responseCourseModel.getCategories().size());
    }

    @Test
    public void testDeleteCourseWithoutAuthentication() throws Exception {
        this.resetSession();

        Course course = createRepositoryCourse();

        this.mockMvc.perform(delete(API_URL_COURSE + "/" + course.getCourseId())
                .session(this.mockHttpSession)
                .accept(MediaType.ALL))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteCourseWithAuthentication() throws Exception {
        this.resetSession();

        Course course = createRepositoryCourse();
        String rawPassword = "testPassword123";
        User user = createRepositoryUser(rawPassword);
        loginUser(user, rawPassword);

        this.mockMvc.perform(delete(API_URL_COURSE + "/" + course.getCourseId())
                .session(this.mockHttpSession)
                .accept(MediaType.ALL))
                .andExpect(status().isNoContent());
    }

    private Course createRepositoryCourse() {
        // Setup course
        Course course = new Course(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
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
