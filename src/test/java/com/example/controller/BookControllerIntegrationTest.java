package com.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dto.book.BookDto;
import com.example.dto.book.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerIntegrationTest {
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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/delete-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = createBook();
        BookDto expected = createBookDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/books")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void createBook_InValidRequestDto_Failed() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Book 1")
                .setAuthor("Author 1")
                .setPrice(BigDecimal.valueOf(400));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    @Sql(scripts = "classpath:database/add-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllBooks_Ok() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> expected = new ArrayList<>();
        expected.add(createBookDto());
        BookDto[] actual = objectMapper.readValue(mvcResult
                .getResponse()
                .getContentAsString(), BookDto[].class);
        assertEquals(expected, List.of(actual));
    }

    @Test
    @WithMockUser
    @Sql(scripts = "classpath:database/add-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void getBookById_Ok() throws Exception {
        long id = 1;
        MvcResult result = mockMvc.perform(get("/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto expected = createBookDto();
        BookDto actual = objectMapper.readValue(result
                .getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertEquals(id, actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser
    public void getBookById5_NotOk() {
        long id = 5;
        assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/books/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON));
        }, "Can't find book by id: " + id);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/delete-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateBook_Ok() throws Exception {
        long id = 1;
        BookDto updateBookRequestDto = createBookDto().setIsbn("12345");
        String request = objectMapper.writeValueAsString(updateBookRequestDto);
        MvcResult mvcResult = mockMvc.perform(
                        put("/books/{id}", id)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto expected = createBookDto()
                .setId(id)
                .setIsbn("12345");
        BookDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateBook_NotOk_NotFound() throws Exception {
        long id = 5;
        CreateBookRequestDto updateBookRequestDto = createBook()
                .setIsbn("123456789990").setTitle("Updated book");
        String request = objectMapper.writeValueAsString(updateBookRequestDto);
        mockMvc.perform(
                        put("/books/{id}", id)
                                .content(request))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    @Sql(scripts = "classpath:database/add-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void searchBookByParameters_Ok() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/books/search")
                                .param("authors", "Author 1")
                                .param("titles", "Book 1")
                                .param("isbns", "13245768")
                                .param("prices", String.valueOf(BigDecimal.valueOf(350.05))))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> expected = new ArrayList<>();
        expected.add(createBookDto());
        BookDto[] actual = objectMapper.readValue(mvcResult
                .getResponse()
                .getContentAsString(), BookDto[].class);
        assertEquals(expected, List.of(actual));
    }

    @Test
    @WithMockUser
    @Sql(scripts = "classpath:database/add-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void searchBookByParameters_Ok_EmptyList() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/books/search")
                                .param("authors", "ignre"))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> actual = objectMapper.readValue(mvcResult
                .getResponse().getContentAsString(), ArrayList.class);
        assertEquals(new ArrayList<>(), actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBook_ValidId_Success() throws Exception {
        long id = 1;
        mockMvc.perform(delete("/books/{id}", id))
                .andExpect(status().isNoContent());
    }

    private static CreateBookRequestDto createBook() {
        return new CreateBookRequestDto()
                .setTitle("Book 1")
                .setAuthor("Author 1")
                .setIsbn("13245768")
                .setPrice(BigDecimal.valueOf(350.05));
    }

    private static BookDto createBookDto() {
        return new BookDto()
                .setId(1L)
                .setTitle("Book 1")
                .setAuthor("Author 1")
                .setIsbn("13245768")
                .setPrice(BigDecimal.valueOf(350.05))
                .setCategoryIds(List.of(1L));
    }
}
