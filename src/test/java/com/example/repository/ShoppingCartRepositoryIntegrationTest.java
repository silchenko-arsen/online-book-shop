package com.example.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.model.Role;
import com.example.model.ShoppingCart;
import com.example.model.User;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryIntegrationTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @Sql(scripts = "classpath:database/add-for-shopping_cart-cart_item-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-for-shopping_cart-cart_item-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findShoppingCartByUserId_Returns_Ok() {
        long id = 1L;
        Role role = new Role();
        role.setId(id);
        role.setName(Role.RoleName.ADMIN);
        User user = new User();
        user.setId(id);
        user.setPassword("$2a$10$l7wlcrpU7ncwbVK/qMUafe7gSFrzpXz3xj4Y3tOJo7BgQZT4rxMaq");
        user.setFirstName("Arsen");
        user.setLastName("Silchenko");
        user.setEmail("silchenkoarsen@gmail.com");
        user.setShippingAddress("23 Downton St.");
        user.setRoles(Set.of(role));
        ShoppingCart expected = new ShoppingCart();
        expected.setId(id);
        expected.setUser(user);
        Optional<ShoppingCart> actual = shoppingCartRepository.findShoppingCartByUserId(id);
        assertEquals(expected, actual.get());
    }

    @Test
    @Sql(scripts = "classpath:database/add-for-shopping_cart-cart_item-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-for-shopping_cart-cart_item-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findShoppingCartByUserId_ReturnsEmpty() {
        long id = 5;
        Optional<ShoppingCart> actual = shoppingCartRepository.findShoppingCartByUserId(id);
        assertTrue(actual.isEmpty());
    }
}
