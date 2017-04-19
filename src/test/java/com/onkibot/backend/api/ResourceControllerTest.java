package com.onkibot.backend.api;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import java.io.IOException;
import java.util.List;
import java.util.UUID;
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

// TODO: Add tests for DELETE

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(OnkibotBackendApplication.class)
@EnableJpaRepositories
@WebAppConfiguration
@Sql(
  executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
  scripts = "classpath:./beforeTestRun.sql"
)
public class ResourceControllerTest {
  private static final String API_PATH_RESOURCE = "resources";
  private static final String API_PATH_CATEGORY = "categories";
  private static final String API_URL_COURSE = OnkibotBackendApplication.API_BASE_URL + "/courses";

  private MockMvc mockMvc;

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private CourseRepository courseRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private ResourceRepository resourceRepository;

  @Before
  public void setup() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
            .apply(springSecurity())
            .build();
  }

  @Test
  @WithMockUser(
    username = "test@onkibot.com",
    authorities = {"USER"}
  )
  public void testGetNonExistingResourceWithNonExistingCategory() throws Exception {
    Course course = createRepositoryCourse();

    this.mockMvc
        .perform(
            get(API_URL_COURSE
                    + "/"
                    + course.getCourseId()
                    + "/"
                    + API_PATH_CATEGORY
                    + "/2/"
                    + API_PATH_RESOURCE)
                .accept(MediaType.ALL))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(
    username = "test@onkibot.com",
    authorities = {"USER"}
  )
  public void testGetExistingResourceWithWrongCategory() throws Exception {
    User publisherUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category1 = createRepositoryCategory(course);
    Category category2 = createRepositoryCategory(course);
    createRepositoryResource(category1, publisherUser);
    Resource resource2 = createRepositoryResource(category2, publisherUser);

    this.mockMvc
        .perform(
            get(API_URL_COURSE
                    + "/"
                    + course.getCourseId()
                    + "/"
                    + API_PATH_CATEGORY
                    + "/"
                    + category1.getCategoryId()
                    + "/"
                    + API_PATH_RESOURCE
                    + "/"
                    + resource2.getResourceId())
                .accept(MediaType.ALL))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(
    username = "test@onkibot.com",
    authorities = {"USER"}
  )
  public void testGetExistingResourceListWithWrongCategory() throws Exception {
    User publisherUser = createRepositoryUser();
    Course course1 = createRepositoryCourse();
    Course course2 = createRepositoryCourse();
    Category category1 = createRepositoryCategory(course1);
    Category category2 = createRepositoryCategory(course2);
    createRepositoryResource(category1, publisherUser);
    createRepositoryResource(category2, publisherUser);

    this.mockMvc
        .perform(
            get(API_URL_COURSE
                    + "/"
                    + course1.getCourseId()
                    + "/"
                    + API_PATH_CATEGORY
                    + "/"
                    + category2.getCategoryId()
                    + "/"
                    + API_PATH_RESOURCE)
                .accept(MediaType.ALL))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testGetNonExistingResourceWithoutAuthentication() throws Exception {
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    this.mockMvc
        .perform(
            get(API_URL_COURSE
                    + "/"
                    + course.getCourseId()
                    + "/"
                    + API_PATH_CATEGORY
                    + "/"
                    + category.getCategoryId()
                    + "/"
                    + API_PATH_RESOURCE
                    + "/2")
                .accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
    username = "test@onkibot.com",
    authorities = {"USER"}
  )
  public void testGetNonExistingResourceWithAuthentication() throws Exception {
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    this.mockMvc
        .perform(
            get(API_URL_COURSE
                    + "/"
                    + course.getCourseId()
                    + "/"
                    + API_PATH_CATEGORY
                    + "/"
                    + category.getCategoryId()
                    + "/"
                    + API_PATH_RESOURCE
                    + "/2")
                .accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testGetResourceWithoutAuthentication() throws Exception {
    User publisherUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, publisherUser);

    this.mockMvc
        .perform(
            get(API_URL_COURSE
                    + "/"
                    + course.getCourseId()
                    + "/"
                    + API_PATH_CATEGORY
                    + "/"
                    + category.getCategoryId()
                    + "/"
                    + API_PATH_RESOURCE
                    + "/"
                    + resource.getResourceId())
                .accept(MediaType.ALL))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
    username = "test@onkibot.com",
    authorities = {"USER"}
  )
  public void testGetResourceWithAuthentication() throws Exception {
    User publisherUser = createRepositoryUser();
    MockHttpSession mockHttpSession = getAuthenticatedSession(publisherUser);
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, publisherUser);

    MvcResult result =
        this.mockMvc
            .perform(
                get(API_URL_COURSE
                        + "/"
                        + course.getCourseId()
                        + "/"
                        + API_PATH_CATEGORY
                        + "/"
                        + category.getCategoryId()
                        + "/"
                        + API_PATH_RESOURCE
                        + "/"
                        + resource.getResourceId())
                    .session(mockHttpSession)
                    .accept(MediaType.ALL))
            .andExpect(status().isOk())
            .andReturn();

    String jsonString = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();

    assertResponseModel(resource, mapper.readValue(jsonString, ResourceModel.class));
  }

  @Test
  public void testGetResourcesWithoutAuthentication() throws Exception {
    User publisherUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    createRepositoryResource(category, publisherUser);

    this.mockMvc
        .perform(
            get(API_URL_COURSE
                    + "/"
                    + course.getCourseId()
                    + "/"
                    + API_PATH_CATEGORY
                    + "/"
                    + category.getCategoryId()
                    + "/"
                    + API_PATH_RESOURCE)
                .accept(MediaType.ALL))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
    username = "test@onkibot.com",
    authorities = {"USER"}
  )
  public void testGetResourcesWithAuthentication() throws Exception {
    User publisherUser = createRepositoryUser();
    MockHttpSession mockHttpSession = getAuthenticatedSession(publisherUser);
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource1 = createRepositoryResource(category, publisherUser);
    Resource resource2 = createRepositoryResource(category, publisherUser);

    MvcResult result =
        this.mockMvc
            .perform(
                get(API_URL_COURSE
                        + "/"
                        + course.getCourseId()
                        + "/"
                        + API_PATH_CATEGORY
                        + "/"
                        + category.getCategoryId()
                        + "/"
                        + API_PATH_RESOURCE)
                    .session(mockHttpSession)
                    .accept(MediaType.ALL))
            .andExpect(status().isOk())
            .andReturn();

    String jsonString = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    List<ResourceModel> responseResources =
        mapper.readValue(jsonString, new TypeReference<List<ResourceModel>>() {});

    assertEquals(responseResources.size(), 2);

    assertResponseModel(resource1, responseResources.get(0));
    assertResponseModel(resource2, responseResources.get(1));
  }

  @Test
  public void testCreateResourceWithoutAuthentication() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);

    ResourceInputModel resourceInputModel =
        new ResourceInputModel("Random resource", "Random body");

    this.mockMvc
        .perform(
            post(API_URL_COURSE
                    + "/"
                    + course.getCourseId()
                    + "/"
                    + API_PATH_CATEGORY
                    + "/"
                    + category.getCategoryId()
                    + "/"
                    + API_PATH_RESOURCE)
                .content(mapper.writeValueAsString(resourceInputModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
    username = "test@onkibot.com",
    authorities = {"USER"}
  )
  public void testCreateResourceWithAuthentication() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    User publisherUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);

    ResourceInputModel resourceInputModel =
        new ResourceInputModel("Random resource", "Random body");

    MockHttpSession mockHttpSession =
        new MockHttpSession(
            webApplicationContext.getServletContext(), UUID.randomUUID().toString());
    mockHttpSession.setAttribute("userId", publisherUser.getUserId());

    MvcResult categoryCreationResult =
        this.mockMvc
            .perform(
                post(API_URL_COURSE
                        + "/"
                        + course.getCourseId()
                        + "/"
                        + API_PATH_CATEGORY
                        + "/"
                        + category.getCategoryId()
                        + "/"
                        + API_PATH_RESOURCE)
                    .session(mockHttpSession)
                    .content(mapper.writeValueAsString(resourceInputModel))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andReturn();

    assertInputModel(
        mapper,
        category,
        publisherUser,
        resourceInputModel,
        categoryCreationResult.getResponse().getContentAsString());
  }

  private Resource createRepositoryResource(Category category, User publisherUser) {
    // Setup resource
    Resource resource =
        new Resource(
            category, UUID.randomUUID().toString(), UUID.randomUUID().toString(), publisherUser);
    resourceRepository.save(resource);
    return resource;
  }

  private Category createRepositoryCategory(Course course) {
    // Setup category
    Category category =
        new Category(course, UUID.randomUUID().toString(), UUID.randomUUID().toString());
    categoryRepository.save(category);
    return category;
  }

  private Course createRepositoryCourse() {
    // Setup course
    Course course = new Course(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    courseRepository.save(course);
    return course;
  }

  private User createRepositoryUser() {
    String rawPassword = "testPassword123";
    String encodedPassword = passwordEncoder.encode(rawPassword);
    User user = new User("test@onkibot.com", encodedPassword, "OnkiBOT Tester", true);
    userRepository.save(user);
    return user;
  }

  private void assertResponseModel(Resource resource, ResourceModel responseModel)
      throws IOException {
    assertEquals((int) resource.getResourceId(), responseModel.getResourceId());
    assertEquals((int) resource.getCategory().getCategoryId(), responseModel.getCategoryId());
    assertEquals(
        (int) resource.getPublisherUser().getUserId(),
        responseModel.getPublisherUser().getUserId());
    assertEquals(resource.getName(), responseModel.getName());
    assertEquals(resource.getBody(), responseModel.getBody());
    assertEquals(0, resource.getExternalResources().size());
  }

  private void assertInputModel(
      ObjectMapper mapper,
      Category category,
      User publisherUser,
      ResourceInputModel resourceInputModel,
      String jsonString)
      throws IOException {
    ResourceModel responseResourceModel = mapper.readValue(jsonString, ResourceModel.class);

    assertEquals(1, responseResourceModel.getResourceId());
    assertEquals((int) category.getCategoryId(), responseResourceModel.getCategoryId());
    assertEquals(
        (int) publisherUser.getUserId(), responseResourceModel.getPublisherUser().getUserId());
    assertEquals(resourceInputModel.getName(), responseResourceModel.getName());
    assertEquals(resourceInputModel.getBody(), responseResourceModel.getBody());
  }

  private MockHttpSession getAuthenticatedSession(User user) {
    MockHttpSession mockHttpSession =
        new MockHttpSession(
            webApplicationContext.getServletContext(), UUID.randomUUID().toString());
    OnkibotBackendApplication.setSessionUser(user, mockHttpSession);
    return mockHttpSession;
  }
}
