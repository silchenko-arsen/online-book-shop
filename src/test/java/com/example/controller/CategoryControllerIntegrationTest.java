package com.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dto.book.BookDto;
import com.example.dto.category.CategoryDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerIntegrationTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource,
                          @Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-for-book-category-tests.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/delete-for-book-category-tests.sql")
            );
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/delete-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createCategory_ValidRequestDto_Success() throws Exception {
        CategoryDto dto = new CategoryDto()
                .setId(1L)
                .setName("Horror");
        String jsonRequest = objectMapper.writeValueAsString(dto);
        CategoryDto expected = new CategoryDto()
                .setId(1L)
                .setName("Horror");
        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Sql(scripts = "classpath:database/delete-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCategory_InvalidData_BadRequest() throws Exception {
        CategoryDto invalidCategoryRequest = new CategoryDto();
        String jsonRequest = objectMapper.writeValueAsString(invalidCategoryRequest);
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @Sql(scripts = "classpath:database/add-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void getAllCategories_ReturnListOfExistingCategories() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/categories"))
                .andExpect(status().isOk())
                .andReturn();
        List<CategoryDto> expected = new ArrayList<>();
        expected.add(new CategoryDto().setId(1L).setName("Fiction"));
        CategoryDto[] actual = objectMapper.readValue(mvcResult
                .getResponse()
                .getContentAsString(), CategoryDto[].class);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser
    public void getCategoryById_ValidId_ReturnCategory() throws Exception {
        CategoryDto categoryDto = new CategoryDto()
                .setId(1L)
                .setName("Fiction");
        MvcResult result = mockMvc.perform(
                        get("/categories/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result
                .getResponse().getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        assertEquals(categoryDto, actual);
    }

    @Test
    @WithMockUser
    @Sql(scripts = "classpath:database/add-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void getCategoryById_InValidId_GetError() throws Exception {
        long categoryId = 5;
        mockMvc.perform(get("/category/{id}", categoryId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/add-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateCategory_validId_Success() throws Exception {
        long id = 1;
        CategoryDto updateCategoryRequest = new CategoryDto()
                .setName("Updated category");
        CategoryDto expected = new CategoryDto()
                .setId(1L)
                .setName("Updated category");
        String request = objectMapper.writeValueAsString(updateCategoryRequest);
        MvcResult result = mockMvc.perform(put("/categories/{id}", id)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateCategory_InValidId_ReturnError() throws Exception {
        long id = 5;
        CategoryDto updatedBook = new CategoryDto()
                .setName("new Category")
                .setDescription("Updated description");
        String request = objectMapper.writeValueAsString(updatedBook);
        mockMvc.perform(put("/categories/{id}", id)
                                .content(request))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/delete-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteCategory_Success() throws Exception {
        mockMvc.perform(delete("/categories/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void getBooksByCategoryId_Success() throws Exception {
        Long categoryId = 1L;
        MvcResult mvcResult = mockMvc.perform(
                        get("/categories/{categoryId}/books", categoryId))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setId(categoryId).setTitle("Book 1")
                .setPrice(BigDecimal.valueOf(350.05)).setIsbn("13245768").setAuthor("Author 1"));
        BookDto[] actual = objectMapper.readValue(mvcResult
                .getResponse()
                .getContentAsString(), BookDto[].class);
        assertEquals(expected, List.of(actual));
    }

    @Test
    @WithMockUser
    public void getAllBooksByCategoryId_Failed() throws Exception {
        Long categoryId = 100L;
        MvcResult mvcResult = mockMvc.perform(
                        get("/categories/{categoryId}/books", categoryId))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> expected = new ArrayList<>();
        BookDto[] actual = objectMapper.readValue(mvcResult
                .getResponse()
                .getContentAsString(), BookDto[].class);
        assertEquals(expected, List.of(actual));
    }
}

