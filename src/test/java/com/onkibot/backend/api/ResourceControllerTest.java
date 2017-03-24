package com.onkibot.backend.api;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.Category;
import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.entities.Resource;
import com.onkibot.backend.database.entities.User;
import com.onkibot.backend.database.repositories.CategoryRepository;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.database.repositories.ResourceRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// TODO: Add tests for external resources
// TODO: Add tests for DELETE


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(OnkibotBackendApplication.class)
@EnableJpaRepositories
@WebAppConfiguration
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:./beforeTestRun.sql")
public class ResourceControllerTest {
    private final static String API_PATH_RESOURCE = "resources";
    private final static String API_PATH_CATEGORY = "categories";
    private final static String API_URL_COURSE = OnkibotBackendApplication.API_BASE_URL + "/courses";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testGetNonExistingResourceWithoutAuthentication() throws Exception {
        Course course = createRepositoryCourse();
        Category category = createRepositoryCategory(course);
        this.mockMvc.perform(get(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH_CATEGORY + "/" + category.getCategoryId() + "/" + API_PATH_RESOURCE + "/2")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void testGetNonExistingResourceWithAuthentication() throws Exception {
        Course course = createRepositoryCourse();
        Category category = createRepositoryCategory(course);
        this.mockMvc.perform(get(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH_CATEGORY + "/" + category.getCategoryId() + "/" + API_PATH_RESOURCE + "/2")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetResourceWithoutAuthentication() throws Exception {
        User publisherUser = createRepositoryUser();
        Course course = createRepositoryCourse();
        Category category = createRepositoryCategory(course);
        Resource resource = createRepositoryResource(category, publisherUser);

        this.mockMvc.perform(get(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH_CATEGORY + "/" + category.getCategoryId() + "/" + API_PATH_RESOURCE + "/" + resource.getResourceId())
                .accept(MediaType.ALL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void testGetResourceWithAuthentication() throws Exception {
        User publisherUser = createRepositoryUser();
        Course course = createRepositoryCourse();
        Category category = createRepositoryCategory(course);
        Resource resource = createRepositoryResource(category, publisherUser);

        MvcResult result = this.mockMvc.perform(get(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH_CATEGORY + "/" + category.getCategoryId() + "/" + API_PATH_RESOURCE + "/" + resource.getResourceId())
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String jsonString = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        ResourceModel responseResourceModel = mapper.readValue(jsonString, ResourceModel.class);

        assertEquals((int) resource.getResourceId(), responseResourceModel.getResourceId());
        assertEquals((int) resource.getCategory().getCategoryId(), responseResourceModel.getCategoryId());
        assertEquals((int) publisherUser.getUserId(), responseResourceModel.getPublisherUser().getUserId());
        assertEquals(resource.getName(), responseResourceModel.getName());
        assertEquals(resource.getBody(), responseResourceModel.getBody());
        assertEquals(0, resource.getExternalResources().size());
    }

    @Test
    public void testGetResourcesWithoutAuthentication() throws Exception {
        User publisherUser = createRepositoryUser();
        Course course = createRepositoryCourse();
        Category category = createRepositoryCategory(course);
        createRepositoryResource(category, publisherUser);

        this.mockMvc.perform(get(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH_CATEGORY + "/" + category.getCategoryId() + "/" + API_PATH_RESOURCE)
                .accept(MediaType.ALL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void testGetResourcesWithAuthentication() throws Exception {
        User publisherUser = createRepositoryUser();
        Course course = createRepositoryCourse();
        Category category = createRepositoryCategory(course);
        Resource resource1 = createRepositoryResource(category, publisherUser);
        Resource resource2 = createRepositoryResource(category, publisherUser);

        MvcResult result = this.mockMvc.perform(get(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH_CATEGORY + "/" + category.getCategoryId() + "/" + API_PATH_RESOURCE)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String jsonString = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<ResourceModel> responseResources = mapper.readValue(
                jsonString,
                new TypeReference<List<ResourceModel>>(){}
        );

        assertEquals(responseResources.size(), 2);

        ResourceModel responseResourceModel1 = responseResources.get(0);
        ResourceModel responseResourceModel2 = responseResources.get(1);

        assertEquals((int) resource1.getResourceId(), responseResourceModel1.getResourceId());
        assertEquals((int) resource1.getCategory().getCategoryId(), responseResourceModel1.getCategoryId());
        assertEquals((int) publisherUser.getUserId(), responseResourceModel1.getPublisherUser().getUserId());
        assertEquals(resource1.getName(), responseResourceModel1.getName());
        assertEquals(resource1.getBody(), responseResourceModel1.getBody());
        assertEquals(0, resource1.getExternalResources().size());

        assertEquals((int) resource2.getResourceId(), responseResourceModel2.getResourceId());
        assertEquals((int) resource2.getCategory().getCategoryId(), responseResourceModel2.getCategoryId());
        assertEquals((int) publisherUser.getUserId(), responseResourceModel2.getPublisherUser().getUserId());
        assertEquals(resource2.getName(), responseResourceModel2.getName());
        assertEquals(resource2.getBody(), responseResourceModel2.getBody());
        assertEquals(0, resource2.getExternalResources().size());
    }

    @Test
    public void testCreateResourceWithoutAuthentication() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Course course = createRepositoryCourse();
        Category category = createRepositoryCategory(course);

        ResourceInputModel resourceInputModel = new ResourceInputModel(
                "Random resource",
                "Random body"
        );


        this.mockMvc.perform(post(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH_CATEGORY + "/" + category.getCategoryId() + "/" + API_PATH_RESOURCE)
                .content(mapper.writeValueAsString(resourceInputModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@onkibot.com", authorities = {"USER"})
    public void testCreateResourceWithAuthentication() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        User publisherUser = createRepositoryUser();
        Course course = createRepositoryCourse();
        Category category = createRepositoryCategory(course);

        ResourceInputModel resourceInputModel = new ResourceInputModel(
                "Random resource",
                "Random body"
        );


        MockHttpSession mockHttpSession = new MockHttpSession(
                webApplicationContext.getServletContext(),
                UUID.randomUUID().toString()
        );
        mockHttpSession.setAttribute("userId", publisherUser.getUserId());


        MvcResult categoryCreationResult = this.mockMvc.perform(post(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH_CATEGORY + "/" + category.getCategoryId() + "/" + API_PATH_RESOURCE)
                .session(mockHttpSession)
                .content(mapper.writeValueAsString(resourceInputModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonString = categoryCreationResult.getResponse().getContentAsString();

        ResourceModel responseResourceModel = mapper.readValue(jsonString, ResourceModel.class);

        assertEquals(1, responseResourceModel.getResourceId());
        assertEquals((int) category.getCategoryId(), responseResourceModel.getCategoryId());
        assertEquals((int) publisherUser.getUserId(), responseResourceModel.getPublisherUser().getUserId());
        assertEquals(resourceInputModel.getName(), responseResourceModel.getName());
        assertEquals(resourceInputModel.getBody(), responseResourceModel.getBody());
    }

    private Resource createRepositoryResource(Category category, User publisherUser) {
        // Setup resource
        Resource resource = new Resource(
                category,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                publisherUser
        );
        resourceRepository.save(resource);
        return resource;
    }

    private Category createRepositoryCategory(Course course) {
        // Setup category
        Category category = new Category(
                course,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
        categoryRepository.save(category);
        return category;
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

    private User createRepositoryUser() {
        String rawPassword = "testPassword123";
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
}
