package com.onkibot.backend.api;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkibot.backend.OnkibotBackendApplication;
import com.onkibot.backend.database.entities.Category;
import com.onkibot.backend.database.entities.Course;
import com.onkibot.backend.database.repositories.CategoryRepository;
import com.onkibot.backend.database.repositories.CourseRepository;
import com.onkibot.backend.models.*;
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
public class CategoryControllerTest {
    private final static String API_PATH = "categories";
    private final static String API_URL_COURSE = OnkibotBackendApplication.API_BASE_URL + "/courses";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void testGetExistingCategoryWithWrongCourse() throws Exception {
        Course course1 = createRepositoryCourse();
        Course course2 = createRepositoryCourse();
        createRepositoryCategory(course1);
        Category category2 = createRepositoryCategory(course2);

        this.mockMvc.perform(get(API_URL_COURSE + "/" + course1.getCourseId() + "/" + API_PATH + "/" + category2.getCategoryId())
                .accept(MediaType.ALL))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetNonExistingCategoryWithoutAuthentication() throws Exception {
        Course course = createRepositoryCourse();
        this.mockMvc.perform(get(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH + "/2")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void testGetNonExistingCategoryWithAuthentication() throws Exception {
        Course course = createRepositoryCourse();
        this.mockMvc.perform(get(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH + "/2")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCategoryWithoutAuthentication() throws Exception {
        Course course = createRepositoryCourse();
        Category category = createRepositoryCategory(course);

        this.mockMvc.perform(get(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH + "/" + category.getCategoryId())
                .accept(MediaType.ALL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void testGetCategoryWithAuthentication() throws Exception {
        Course course = createRepositoryCourse();
        Category category = createRepositoryCategory(course);

        MvcResult result = this.mockMvc.perform(get(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH + "/" + category.getCategoryId())
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String jsonString = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        CategoryModel responseCategoryModel = mapper.readValue(jsonString, CategoryModel.class);

        assertEquals((int) category.getCategoryId(), responseCategoryModel.getCategoryId());
        assertEquals((int) category.getCourse().getCourseId(), responseCategoryModel.getCourseId());
        assertEquals(category.getName(), responseCategoryModel.getName());
        assertEquals(category.getDescription(), responseCategoryModel.getDescription());
        assertEquals(category.getResources().size(), responseCategoryModel.getResources().size());
    }

    @Test
    public void testGetCategoriesWithoutAuthentication() throws Exception {
        Course course = createRepositoryCourse();

        this.mockMvc.perform(get(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH)
                .accept(MediaType.ALL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void testGetCategoriesWithAuthentication() throws Exception {
        Course course = createRepositoryCourse();
        Category category1 = createRepositoryCategory(course);
        Category category2 = createRepositoryCategory(course);

        MvcResult result = this.mockMvc.perform(get(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String jsonString = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<CategoryModel> responseCategories = mapper.readValue(
                jsonString,
                new TypeReference<List<CategoryModel>>(){}
        );

        assertEquals(responseCategories.size(), 2);

        CategoryModel responseCategoryModel1 = responseCategories.get(0);
        CategoryModel responseCategoryModel2 = responseCategories.get(1);


        assertEquals((int) category1.getCategoryId(), responseCategoryModel1.getCategoryId());
        assertEquals((int) category1.getCourse().getCourseId(), responseCategoryModel1.getCourseId());
        assertEquals(category1.getName(), responseCategoryModel1.getName());
        assertEquals(category1.getDescription(), responseCategoryModel1.getDescription());
        assertEquals(category1.getResources().size(), responseCategoryModel1.getResources().size());


        assertEquals((int) category2.getCategoryId(), responseCategoryModel2.getCategoryId());
        assertEquals((int) category2.getCourse().getCourseId(), responseCategoryModel2.getCourseId());
        assertEquals(category2.getName(), responseCategoryModel2.getName());
        assertEquals(category2.getDescription(), responseCategoryModel2.getDescription());
        assertEquals(category2.getResources().size(), responseCategoryModel2.getResources().size());
    }

    @Test
    public void testCreateCategoryWithoutAuthentication() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Course course = createRepositoryCourse();

        CategoryInputModel categoryInputModel = new CategoryInputModel(
                "Random category",
                "Random description"
        );


        this.mockMvc.perform(post(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH)
                .content(mapper.writeValueAsString(categoryInputModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void testCreateCategoryWithAuthentication() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Course course = createRepositoryCourse();

        CategoryInputModel categoryInputModel = new CategoryInputModel(
                "Random category",
                "Random description"
        );


        MvcResult categoryCreationResult = this.mockMvc.perform(post(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH)
                .content(mapper.writeValueAsString(categoryInputModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonString = categoryCreationResult.getResponse().getContentAsString();

        CategoryModel responseCategoryModel = mapper.readValue(jsonString, CategoryModel.class);

        assertEquals(1, responseCategoryModel.getCategoryId());
        assertEquals((int) course.getCourseId(), responseCategoryModel.getCourseId());
        assertEquals(categoryInputModel.getName(), responseCategoryModel.getName());
        assertEquals(categoryInputModel.getDescription(), responseCategoryModel.getDescription());
        assertEquals(0, responseCategoryModel.getResources().size());
    }

    @Test
    public void testDeleteCategoryWithoutAuthentication() throws Exception {
        Course course = createRepositoryCourse();
        Category category = createRepositoryCategory(course);

        this.mockMvc.perform(delete(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH + "/" + category.getCategoryId())
                .accept(MediaType.ALL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void testDeleteCategoryWithAuthentication() throws Exception {
        Course course = createRepositoryCourse();
        Category category = createRepositoryCategory(course);

        this.mockMvc.perform(delete(API_URL_COURSE + "/" + course.getCourseId() + "/" + API_PATH + "/" + category.getCategoryId())
                .accept(MediaType.ALL))
                .andExpect(status().isNoContent());
    }

    private Category createRepositoryCategory(Course course) {
        // Setup course
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
}
