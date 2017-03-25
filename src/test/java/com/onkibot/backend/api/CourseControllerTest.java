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
import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.models.*;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
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
public class CourseControllerTest {
  private static final String API_URL = OnkibotBackendApplication.API_BASE_URL + "/courses";

  private MockMvc mockMvc;

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private CourseRepository courseRepository;

  @Before
  public void setup() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
            .apply(springSecurity())
            .build();
  }

  @Test
  public void testGetNonExistingResourceWithoutAuthentication() throws Exception {
    this.mockMvc
        .perform(get(API_URL + "/2").accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetNonExistingResourceWithAuthentication() throws Exception {
    this.mockMvc
        .perform(get(API_URL + "/2").accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testGetCourseWithoutAuthentication() throws Exception {
    Course course = createRepositoryCourse();

    this.mockMvc
        .perform(get(API_URL + "/" + course.getCourseId()).accept(MediaType.ALL))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetCourseWithAuthentication() throws Exception {
    Course course = createRepositoryCourse();

    MvcResult result =
        this.mockMvc
            .perform(get(API_URL + "/" + course.getCourseId()).accept(MediaType.ALL))
            .andExpect(status().isOk())
            .andReturn();

    String jsonString = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    CourseModel responseCourseModel = mapper.readValue(jsonString, CourseModel.class);

    assertResponseModel(course, responseCourseModel);
  }

  @Test
  public void testGetCoursesWithoutAuthentication() throws Exception {
    this.mockMvc.perform(get(API_URL).accept(MediaType.ALL)).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testGetCoursesWithAuthentication() throws Exception {
    Course course1 = createRepositoryCourse();
    Course course2 = createRepositoryCourse();

    MvcResult result =
        this.mockMvc
            .perform(get(API_URL).accept(MediaType.ALL))
            .andExpect(status().isOk())
            .andReturn();

    String jsonString = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    List<CourseModel> responseCourses =
        mapper.readValue(jsonString, new TypeReference<List<CourseModel>>() {});

    assertEquals(responseCourses.size(), 2);

    assertResponseModel(course1, responseCourses.get(0));
    assertResponseModel(course2, responseCourses.get(1));
  }

  @Test
  public void testCreateCourseWithoutAuthentication() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    CourseInputModel courseInputModel = new CourseInputModel("Random course", "Random description");

    this.mockMvc
        .perform(
            post(API_URL)
                .content(mapper.writeValueAsString(courseInputModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testCreateCourseWithAuthentication() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    CourseInputModel courseInputModel = new CourseInputModel("Random course", "Random description");

    MvcResult courseCreationResult =
        this.mockMvc
            .perform(
                post(API_URL)
                    .content(mapper.writeValueAsString(courseInputModel))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andReturn();

    String jsonString = courseCreationResult.getResponse().getContentAsString();

    CourseModel responseCourseModel = mapper.readValue(jsonString, CourseModel.class);
    assertEquals(1, responseCourseModel.getCourseId());
    assertEquals(courseInputModel.getName(), responseCourseModel.getName());
    assertEquals(courseInputModel.getDescription(), responseCourseModel.getDescription());
    assertEquals(0, responseCourseModel.getCategories().size());
  }

  @Test
  public void testDeleteCourseWithoutAuthentication() throws Exception {
    Course course = createRepositoryCourse();

    this.mockMvc
        .perform(delete(API_URL + "/" + course.getCourseId()).accept(MediaType.ALL))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  public void testDeleteCourseWithAuthentication() throws Exception {
    Course course = createRepositoryCourse();

    this.mockMvc
        .perform(delete(API_URL + "/" + course.getCourseId()).accept(MediaType.ALL))
        .andExpect(status().isNoContent());
  }

  private Course createRepositoryCourse() {
    // Setup course
    Course course = new Course(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    courseRepository.save(course);
    return course;
  }

  private void assertResponseModel(Course course, CourseModel responseModel) {
    assertEquals((int) course.getCourseId(), responseModel.getCourseId());
    assertEquals(course.getName(), responseModel.getName());
    assertEquals(course.getDescription(), responseModel.getDescription());
    assertEquals(course.getCategories().size(), responseModel.getCategories().size());
    assertEquals(course.getAttendees().size(), responseModel.getAttendees().size());
  }
}
