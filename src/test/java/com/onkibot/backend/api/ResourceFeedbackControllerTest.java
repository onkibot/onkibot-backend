package com.onkibot.backend.api;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.*;
import com.onkibot.backend.database.repositories.*;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(OnkibotBackendApplication.class)
@EnableJpaRepositories
@WebAppConfiguration
@Sql(
  executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
  scripts = "classpath:./beforeTestRun.sql"
)
public class ResourceFeedbackControllerTest {
  private static final String API_PATH_RESOURCE_FEEDBACK = "feedback";
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

  @Autowired private ResourceFeedbackRepository resourceFeedbackRepository;

  @Before
  public void setup() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
            .apply(springSecurity())
            .build();
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetExistingResourceFeedbackWithWrongCategory() throws Exception {
    User feedbackUser = createRepositoryUser();
    Course course1 = createRepositoryCourse();
    Course course2 = createRepositoryCourse();
    Category category1 = createRepositoryCategory(course1);
    Category category2 = createRepositoryCategory(course2);
    Resource resource1 = createRepositoryResource(category1, feedbackUser);
    Resource resource2 = createRepositoryResource(category2, feedbackUser);
    createRepositoryResourceFeedback(resource1, feedbackUser);
    createRepositoryResourceFeedback(resource2, feedbackUser);

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
                    + API_PATH_RESOURCE
                    + "/"
                    + resource1.getResourceId()
                    + "/"
                    + API_PATH_RESOURCE_FEEDBACK)
                .accept(MediaType.ALL))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetNonExistingResourceFeedbackWithNonExistingResource() throws Exception {
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
                    + "/2/"
                    + API_PATH_RESOURCE_FEEDBACK)
                .accept(MediaType.ALL))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetExistingExistingResourceWithWrongCategory() throws Exception {
    User feedbackUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource1 = createRepositoryResource(category, feedbackUser);
    Resource resource2 = createRepositoryResource(category, feedbackUser);
    createRepositoryResourceFeedback(resource1, feedbackUser);
    ResourceFeedback resourceFeedback2 = createRepositoryResourceFeedback(resource2, feedbackUser);

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
                    + resource1.getResourceId()
                    + "/"
                    + API_PATH_RESOURCE_FEEDBACK
                    + "/"
                    + resourceFeedback2.getResourceFeedbackId())
                .accept(MediaType.ALL))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetExistingResourceFeedbackListWithWrongResource() throws Exception {
    User feedbackUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category1 = createRepositoryCategory(course);
    Category category2 = createRepositoryCategory(course);
    Resource resource1 = createRepositoryResource(category1, feedbackUser);
    Resource resource2 = createRepositoryResource(category2, feedbackUser);
    createRepositoryResourceFeedback(resource1, feedbackUser);
    createRepositoryResourceFeedback(resource2, feedbackUser);

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
                    + resource2.getResourceId()
                    + "/"
                    + API_PATH_RESOURCE_FEEDBACK)
                .accept(MediaType.ALL))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testGetNonExistingResourceFeedbackWithoutAuthentication() throws Exception {
    User feedbackUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, feedbackUser);
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
                    + resource.getResourceId()
                    + "/"
                    + API_PATH_RESOURCE_FEEDBACK
                    + "/2")
                .accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetNonExistingResourceWithAuthentication() throws Exception {
    User feedbackUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, feedbackUser);

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
                    + resource.getResourceId()
                    + "/"
                    + API_PATH_RESOURCE_FEEDBACK
                    + "/2")
                .accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testGetResourceFeedbackWithoutAuthentication() throws Exception {
    User feedbackUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, feedbackUser);
    ResourceFeedback resourceFeedback = createRepositoryResourceFeedback(resource, feedbackUser);

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
                    + resource.getResourceId()
                    + "/"
                    + API_PATH_RESOURCE_FEEDBACK
                    + "/"
                    + resourceFeedback.getResourceFeedbackId())
                .accept(MediaType.ALL))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetResourceFeedbackWithAuthentication() throws Exception {
    User feedbackUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, feedbackUser);
    ResourceFeedback resourceFeedback = createRepositoryResourceFeedback(resource, feedbackUser);

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
                        + resource.getResourceId()
                        + "/"
                        + API_PATH_RESOURCE_FEEDBACK
                        + "/"
                        + resourceFeedback.getResourceFeedbackId())
                    .accept(MediaType.ALL))
            .andExpect(status().isOk())
            .andReturn();

    String jsonString = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();

    assertResponseModel(
        resourceFeedback, mapper.readValue(jsonString, ResourceFeedbackModel.class));
  }

  @Test
  public void testGetResourceFeedbacksWithoutAuthentication() throws Exception {
    User feedbackUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, feedbackUser);
    createRepositoryResourceFeedback(resource, feedbackUser);

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
                    + resource.getResourceId()
                    + "/"
                    + API_PATH_RESOURCE_FEEDBACK)
                .accept(MediaType.ALL))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetResourceFeedbacksWithAuthentication() throws Exception {
    User feedbackUser1 = createRepositoryUser();
    User feedbackUser2 = createRepositoryUser("test2@onkibot.com", "OnkiBOT Tester2");
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, feedbackUser1);
    ResourceFeedback resourceFeedback1 = createRepositoryResourceFeedback(resource, feedbackUser1);
    ResourceFeedback resourceFeedback2 = createRepositoryResourceFeedback(resource, feedbackUser2);

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
                        + resource.getResourceId()
                        + "/"
                        + API_PATH_RESOURCE_FEEDBACK)
                    .accept(MediaType.ALL))
            .andExpect(status().isOk())
            .andReturn();

    String jsonString = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    List<ResourceFeedbackModel> responseResourceFeedback =
        mapper.readValue(jsonString, new TypeReference<List<ResourceFeedbackModel>>() {});

    assertEquals(responseResourceFeedback.size(), 2);

    assertResponseModel(resourceFeedback1, responseResourceFeedback.get(0));
    assertResponseModel(resourceFeedback2, responseResourceFeedback.get(1));
  }

  @Test
  public void testCreateResourceFeedbackWithoutAuthentication() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    User feedbackUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, feedbackUser);

    ResourceFeedbackInputModel resourceFeedbackInputModel =
        new ResourceFeedbackInputModel("Nice resource!");

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
                    + API_PATH_RESOURCE
                    + "/"
                    + resource.getResourceId()
                    + "/"
                    + API_PATH_RESOURCE_FEEDBACK)
                .content(mapper.writeValueAsString(resourceFeedbackInputModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
    username = "test@onkibot.com",
    authorities = {"USER"}
  )
  public void testCreateResourceFeedbackWithAuthentication() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    User feedbackUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, feedbackUser);

    ResourceFeedbackInputModel resourceFeedbackInputModel =
        new ResourceFeedbackInputModel("Nice resource!");

    MockHttpSession mockHttpSession =
        new MockHttpSession(
            webApplicationContext.getServletContext(), UUID.randomUUID().toString());
    mockHttpSession.setAttribute("userId", feedbackUser.getUserId());

    MvcResult resourceFeedbackCreationResult =
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
                        + API_PATH_RESOURCE
                        + "/"
                        + resource.getResourceId()
                        + "/"
                        + API_PATH_RESOURCE_FEEDBACK)
                    .session(mockHttpSession)
                    .content(mapper.writeValueAsString(resourceFeedbackInputModel))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andReturn();

    assertInputModel(
        mapper,
        resource,
        feedbackUser,
        resourceFeedbackInputModel,
        resourceFeedbackCreationResult.getResponse().getContentAsString());
  }

  @Test
  public void testDeleteResourceFeedbackWithoutAuthentication() throws Exception {
    User feedbackUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, feedbackUser);
    ResourceFeedback resourceFeedback = createRepositoryResourceFeedback(resource, feedbackUser);

    this.mockMvc
        .perform(
            delete(
                    API_URL_COURSE
                        + "/"
                        + course.getCourseId()
                        + "/"
                        + API_PATH_CATEGORY
                        + "/"
                        + category.getCategoryId()
                        + "/"
                        + API_PATH_RESOURCE
                        + "/"
                        + resource.getResourceId()
                        + "/"
                        + API_PATH_RESOURCE_FEEDBACK
                        + "/"
                        + resourceFeedback.getResourceFeedbackId())
                .accept(MediaType.ALL))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
    username = "test@onkibot.com",
    authorities = {"USER"}
  )
  public void testDeleteResourceFeedbackWithAuthentication() throws Exception {
    User feedbackUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, feedbackUser);
    ResourceFeedback resourceFeedback = createRepositoryResourceFeedback(resource, feedbackUser);

    MockHttpSession mockHttpSession =
        new MockHttpSession(
            webApplicationContext.getServletContext(), UUID.randomUUID().toString());
    mockHttpSession.setAttribute("userId", feedbackUser.getUserId());

    this.mockMvc
        .perform(
            delete(
                    API_URL_COURSE
                        + "/"
                        + course.getCourseId()
                        + "/"
                        + API_PATH_CATEGORY
                        + "/"
                        + category.getCategoryId()
                        + "/"
                        + API_PATH_RESOURCE
                        + "/"
                        + resource.getResourceId()
                        + "/"
                        + API_PATH_RESOURCE_FEEDBACK
                        + "/"
                        + resourceFeedback.getResourceFeedbackId())
                .session(mockHttpSession)
                .accept(MediaType.ALL))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(
    username = "test@onkibot.com",
    authorities = {"USER"}
  )
  public void testDeleteAnotherUserResourceFeedbackWithAuthentication() throws Exception {
    User feedbackUser1 = createRepositoryUser();
    User feedbackUser2 = createRepositoryUser("test2@onkibot.com", "OnkiBOT Tester2");
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, feedbackUser2);
    ResourceFeedback resourceFeedback = createRepositoryResourceFeedback(resource, feedbackUser1);

    MockHttpSession mockHttpSession =
        new MockHttpSession(
            webApplicationContext.getServletContext(), UUID.randomUUID().toString());
    mockHttpSession.setAttribute("userId", feedbackUser2.getUserId());

    this.mockMvc
        .perform(
            delete(
                    API_URL_COURSE
                        + "/"
                        + course.getCourseId()
                        + "/"
                        + API_PATH_CATEGORY
                        + "/"
                        + category.getCategoryId()
                        + "/"
                        + API_PATH_RESOURCE
                        + "/"
                        + resource.getResourceId()
                        + "/"
                        + API_PATH_RESOURCE_FEEDBACK
                        + "/"
                        + resourceFeedback.getResourceFeedbackId())
                .session(mockHttpSession)
                .accept(MediaType.ALL))
        .andExpect(status().isForbidden());
  }

  private ResourceFeedback createRepositoryResourceFeedback(Resource resource, User feedbackUser) {
    // Setup resource feedback
    ResourceFeedback resourceFeedback =
        new ResourceFeedback(resource, UUID.randomUUID().toString(), feedbackUser);
    resourceFeedbackRepository.save(resourceFeedback);
    return resourceFeedback;
  }

  private Resource createRepositoryResource(Category category, User feedbackUser) {
    // Setup resource
    Resource resource =
        new Resource(
            category, UUID.randomUUID().toString(), UUID.randomUUID().toString(), feedbackUser);
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
    return createRepositoryUser("test@onkibot.com", "OnkiBOT Tester");
  }

  private User createRepositoryUser(String email, String username) {
    String rawPassword = "testPassword123";
    String encodedPassword = passwordEncoder.encode(rawPassword);
    User user = new User(email, encodedPassword, username, true);
    userRepository.save(user);
    return user;
  }

  private void assertResponseModel(
      ResourceFeedback resourceFeedback, ResourceFeedbackModel responseModel) throws IOException {
    assertEquals(
        (int) resourceFeedback.getResourceFeedbackId(), responseModel.getResourceFeedbackId());
    assertEquals(
        (int) resourceFeedback.getResource().getResourceId(), responseModel.getResourceId());
    assertEquals(resourceFeedback.getComment(), responseModel.getComment());
  }

  private void assertInputModel(
      ObjectMapper mapper,
      Resource resource,
      User feedbackUser,
      ResourceFeedbackInputModel resourceFeedbackInputModel,
      String jsonString)
      throws IOException {
    ResourceFeedbackModel responseResourceFeedbackModel =
        mapper.readValue(jsonString, ResourceFeedbackModel.class);

    assertEquals(1, responseResourceFeedbackModel.getResourceFeedbackId());
    assertEquals((int) resource.getResourceId(), responseResourceFeedbackModel.getResourceId());
    assertEquals(
        resourceFeedbackInputModel.getComment(), responseResourceFeedbackModel.getComment());
  }
}
