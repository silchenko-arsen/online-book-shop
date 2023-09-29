package com.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.example.dto.cartitem.CartItemDto;
import com.example.dto.cartitem.CartItemQuantityDto;
import com.example.dto.cartitem.CartItemRequestDto;
import com.example.dto.shoppingcart.ShoppingCartDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.util.Set;
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
class ShoppingCartControllerIntegrationTest {
    private static final long ID = 1;
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
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
                    new ClassPathResource("database/delete-for-shopping_cart-cart_item-tests.sql")
            );
        }
    }

    @Test
    @WithMockUser(username = "silchenkoarsen@gmail.com")
    @Sql(scripts = "classpath:database/add-for-shopping_cart-cart_item-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-for-shopping_cart-cart_item-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getShoppingCart_ReturnsShoppingCartDto_Ok() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto expected = createShoppingCartDto();
        ShoppingCartDto actual = objectMapper.readValue(mvcResult
                .getResponse().getContentAsString(), ShoppingCartDto.class);
        assertNotNull(actual);
        assertEquals(ID, actual.getId());
        reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "silchenkoarsen@gmail.com")
    @Sql(scripts = "classpath:database/add-for-method-add_cart_item-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-for-shopping_cart-cart_item-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addCartItem_validCartItem_Ok() throws Exception {
        CartItemRequestDto requestDto = createCartItemRequestDto();
        CartItemDto expected = createCartItemDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CartItemDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CartItemDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "silchenkoarsen@gmail.com")
    @Sql(scripts = "classpath:database/add-for-shopping_cart-cart_item-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-for-shopping_cart-cart_item-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCartItem_ValidQuantity_Ok() throws Exception {
        CartItemQuantityDto updateCategoryRequest = new CartItemQuantityDto()
                .setQuantity(10);
        CartItemDto expected = createCartItemDto().setQuantity(10);
        String request = objectMapper.writeValueAsString(updateCategoryRequest);
        MvcResult result = mockMvc.perform(put("/cart/cart-items/{cartItemId}", ID)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CartItemDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CartItemDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "silchenkoarsen@gmail.com")
    @Sql(scripts = "classpath:database/add-for-shopping_cart-cart_item-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-for-shopping_cart-cart_item-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteCartItem_Ok() throws Exception {
        mockMvc.perform(delete("/cart/cart-items/{cartItemId}", ID))
                .andExpect(status().isNoContent());
    }

    private CartItemRequestDto createCartItemRequestDto() {
        return new CartItemRequestDto()
                .setBookId(ID).setQuantity(2);
    }

    private CartItemDto createCartItemDto() {
        return new CartItemDto().setId(ID)
                .setBookId(ID).setBookTitle("Book 1").setQuantity(2);
    }

    private ShoppingCartDto createShoppingCartDto() {
        return new ShoppingCartDto().setId(ID)
                .setCartItems(Set.of(createCartItemDto())).setUserId(ID);
    }
}
