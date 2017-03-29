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

// TODO: Add tests for DELETE

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(OnkibotBackendApplication.class)
@EnableJpaRepositories
@WebAppConfiguration
@Sql(
  executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
  scripts = "classpath:./beforeTestRun.sql"
)
public class ExternalResourceControllerTest {
  private static final String API_PATH_EXTERNAL_RESOURCE = "externals";
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

  @Autowired private ExternalResourceRepository externalResourceRepository;

  @Before
  public void setup() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
            .apply(springSecurity())
            .build();
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetExistingExternalResourceWithWrongCategory() throws Exception {
    User publisherUser = createRepositoryUser();
    Course course1 = createRepositoryCourse();
    Course course2 = createRepositoryCourse();
    Category category1 = createRepositoryCategory(course1);
    Category category2 = createRepositoryCategory(course2);
    Resource resource1 = createRepositoryResource(category1, publisherUser);
    Resource resource2 = createRepositoryResource(category2, publisherUser);
    createRepositoryExternalResource(resource1, publisherUser);
    createRepositoryExternalResource(resource2, publisherUser);

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
                    + API_PATH_EXTERNAL_RESOURCE)
                .accept(MediaType.ALL))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetNonExistingExternalResourceWithNonExistingResource() throws Exception {
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
                    + API_PATH_EXTERNAL_RESOURCE)
                .accept(MediaType.ALL))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetExistingExistingResourceWithWrongCategory() throws Exception {
    User publisherUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource1 = createRepositoryResource(category, publisherUser);
    Resource resource2 = createRepositoryResource(category, publisherUser);
    createRepositoryExternalResource(resource1, publisherUser);
    ExternalResource externalResource2 = createRepositoryExternalResource(resource2, publisherUser);

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
                    + API_PATH_EXTERNAL_RESOURCE
                    + "/"
                    + externalResource2.getExternalResourceId())
                .accept(MediaType.ALL))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetExistingExternalResourceListWithWrongResource() throws Exception {
    User publisherUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category1 = createRepositoryCategory(course);
    Category category2 = createRepositoryCategory(course);
    Resource resource1 = createRepositoryResource(category1, publisherUser);
    Resource resource2 = createRepositoryResource(category2, publisherUser);
    createRepositoryExternalResource(resource1, publisherUser);
    createRepositoryExternalResource(resource2, publisherUser);

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
                    + API_PATH_EXTERNAL_RESOURCE)
                .accept(MediaType.ALL))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testGetNonExistingExternalResourceWithoutAuthentication() throws Exception {
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
                    + resource.getResourceId()
                    + "/"
                    + API_PATH_EXTERNAL_RESOURCE
                    + "/2")
                .accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetNonExistingResourceWithAuthentication() throws Exception {
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
                    + resource.getResourceId()
                    + "/"
                    + API_PATH_EXTERNAL_RESOURCE
                    + "/2")
                .accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testGetExternalResourceWithoutAuthentication() throws Exception {
    User publisherUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, publisherUser);
    ExternalResource externalResource = createRepositoryExternalResource(resource, publisherUser);

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
                    + API_PATH_EXTERNAL_RESOURCE
                    + "/"
                    + externalResource.getExternalResourceId())
                .accept(MediaType.ALL))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetExternalResourceWithAuthentication() throws Exception {
    User publisherUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, publisherUser);
    ExternalResource externalResource = createRepositoryExternalResource(resource, publisherUser);

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
                        + API_PATH_EXTERNAL_RESOURCE
                        + "/"
                        + externalResource.getExternalResourceId())
                    .accept(MediaType.ALL))
            .andExpect(status().isOk())
            .andReturn();

    String jsonString = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();

    assertResponseModel(
        externalResource, mapper.readValue(jsonString, ExternalResourceModel.class));
  }

  @Test
  public void testGetExternalResourcesWithoutAuthentication() throws Exception {
    User publisherUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, publisherUser);
    createRepositoryExternalResource(resource, publisherUser);

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
                    + API_PATH_EXTERNAL_RESOURCE)
                .accept(MediaType.ALL))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetExternalResourcesWithAuthentication() throws Exception {
    User publisherUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, publisherUser);
    ExternalResource externalResource1 = createRepositoryExternalResource(resource, publisherUser);
    ExternalResource externalResource2 = createRepositoryExternalResource(resource, publisherUser);

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
                        + API_PATH_EXTERNAL_RESOURCE)
                    .accept(MediaType.ALL))
            .andExpect(status().isOk())
            .andReturn();

    String jsonString = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    List<ExternalResourceModel> externalResponseResources =
        mapper.readValue(jsonString, new TypeReference<List<ExternalResourceModel>>() {});

    assertEquals(externalResponseResources.size(), 2);

    assertResponseModel(externalResource1, externalResponseResources.get(0));
    assertResponseModel(externalResource2, externalResponseResources.get(1));
  }

  @Test
  public void testCreateExternalResourceWithoutAuthentication() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    User publisherUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, publisherUser);

    ExternalResourceInputModel externalResourceInputModel =
        new ExternalResourceInputModel("https://google.com");

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
                    + API_PATH_EXTERNAL_RESOURCE)
                .content(mapper.writeValueAsString(externalResourceInputModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
    username = "test@onkibot.com",
    authorities = {"USER"}
  )
  public void testCreateExternalResourceWithAuthentication() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    User publisherUser = createRepositoryUser();
    Course course = createRepositoryCourse();
    Category category = createRepositoryCategory(course);
    Resource resource = createRepositoryResource(category, publisherUser);

    ExternalResourceInputModel externalResourceInputModel =
        new ExternalResourceInputModel("https://google.com");

    MockHttpSession mockHttpSession =
        new MockHttpSession(
            webApplicationContext.getServletContext(), UUID.randomUUID().toString());
    mockHttpSession.setAttribute("userId", publisherUser.getUserId());

    MvcResult externalResourceCreationResult =
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
                        + API_PATH_EXTERNAL_RESOURCE)
                    .session(mockHttpSession)
                    .content(mapper.writeValueAsString(externalResourceInputModel))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andReturn();

    assertInputModel(
        mapper,
        resource,
        publisherUser,
        externalResourceInputModel,
        externalResourceCreationResult.getResponse().getContentAsString());
  }

  private ExternalResource createRepositoryExternalResource(Resource resource, User publisherUser) {
    // Setup external resource
    ExternalResource externalResource =
        new ExternalResource(resource, UUID.randomUUID().toString(), publisherUser);
    externalResourceRepository.save(externalResource);
    return externalResource;
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

  private void assertResponseModel(
      ExternalResource externalResource, ExternalResourceModel responseModel) throws IOException {
    assertEquals(
        (int) externalResource.getExternalResourceId(), responseModel.getExternalResourceId());
    assertEquals(
        (int) externalResource.getResource().getResourceId(), responseModel.getResourceId());
    assertEquals(
        (int) externalResource.getPublisherUser().getUserId(),
        responseModel.getPublisherUser().getUserId());
    assertEquals(externalResource.getUrl(), responseModel.getUrl());
  }

  private void assertInputModel(
      ObjectMapper mapper,
      Resource resource,
      User publisherUser,
      ExternalResourceInputModel externalResourceInputModel,
      String jsonString)
      throws IOException {
    ExternalResourceModel responseExternalResourceModel =
        mapper.readValue(jsonString, ExternalResourceModel.class);

    assertEquals(1, responseExternalResourceModel.getExternalResourceId());
    assertEquals((int) resource.getResourceId(), responseExternalResourceModel.getResourceId());
    assertEquals(
        (int) publisherUser.getUserId(),
        responseExternalResourceModel.getPublisherUser().getUserId());
    assertEquals(externalResourceInputModel.getUrl(), responseExternalResourceModel.getUrl());
  }
}
