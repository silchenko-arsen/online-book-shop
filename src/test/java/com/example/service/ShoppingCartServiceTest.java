package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.dto.cartitem.CartItemDto;
import com.example.dto.cartitem.CartItemQuantityDto;
import com.example.dto.cartitem.CartItemRequestDto;
import com.example.dto.shoppingcart.ShoppingCartDto;
import com.example.mapper.CartItemMapper;
import com.example.mapper.ShoppingCartMapper;
import com.example.model.Book;
import com.example.model.CartItem;
import com.example.model.ShoppingCart;
import com.example.model.User;
import com.example.repository.BookRepository;
import com.example.repository.CartItemRepository;
import com.example.repository.ShoppingCartRepository;
import com.example.repository.UserRepository;
import com.example.service.impl.ShoppingCartServiceImpl;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    private static final long ID = 1;
    private static final String EMAIL = "jonh.doe@gmail.com";

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private BookRepository bookRepository;
    private ShoppingCart shoppingCart;
    private CartItem cartItem;
    private User user;
    private Book book;

    @BeforeEach
    public void setUp() {
        book = new Book();
        book.setId(ID);
        book.setTitle("Book 1");
        user = new User();
        user.setId(ID);
        user.setEmail(EMAIL);
        shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        cartItem = new CartItem();
        cartItem.setId(ID);
        cartItem.setQuantity(2);
        cartItem.setBook(book);
        cartItem.setShoppingCart(shoppingCart);
    }

    @Test
    void getShoppingCart_ValidEmail_Ok() {
        shoppingCart.setCartItems(Set.of(cartItem));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(ID)).thenReturn(
                Optional.of(shoppingCart));
        ShoppingCartDto expected = createShoppingCartDto();
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        ShoppingCartDto actual = shoppingCartService.getShoppingCartByUserEmail(EMAIL);
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).findShoppingCartByUserId(ID);
        verify(userRepository, times(1)).findByEmail(EMAIL);
    }

    @Test
    void addCartItem_ValidCartItem_Ok() {
        CartItemRequestDto cartItemRequestDto = createCartItemRequestDto();
        CartItemDto expected = createCartItemDto();
        when(cartItemMapper.toEntity(cartItemRequestDto)).thenReturn(cartItem);
        when(bookRepository.findById(ID)).thenReturn(Optional.of(book));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(ID))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);
        CartItemDto actual = shoppingCartService.addCartItem(EMAIL, cartItemRequestDto);
        assertEquals(expected, actual);
        verify(cartItemRepository, times(1)).save(cartItem);
        verify(userRepository, times(1)).findByEmail(EMAIL);
        verify(bookRepository, times(1)).findById(ID);
    }

    @Test
    void updateBookQuantity_ValidQuantity_Ok() {
        cartItem.setQuantity(5);
        when(cartItemRepository.findById(ID)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        CartItemDto expected = createCartItemDto().setQuantity(5);
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);
        CartItemQuantityDto cartItemQuantityRequestDto = new CartItemQuantityDto()
                .setQuantity(5);
        CartItemDto actual = shoppingCartService.updateBookQuantity(ID, cartItemQuantityRequestDto);
        assertEquals(expected, actual);
        verify(cartItemRepository, times(1)).findById(ID);
        verify(cartItemRepository, times(1)).save(cartItem);
    }

    @Test
    public void deleteCartItemById_ValidId_Ok() {
        shoppingCartService.deleteCartItemById(ID);
        verify(cartItemRepository, times(1)).deleteById(ID);
    }

    private CartItemRequestDto createCartItemRequestDto() {
        return new CartItemRequestDto()
                .setBookId(ID).setQuantity(2);
    }

    private CartItemDto createCartItemDto() {
        return new CartItemDto().setId(ID)
                .setBookId(ID).setBookTitle(book.getTitle()).setQuantity(2);
    }

    private ShoppingCartDto createShoppingCartDto() {
        return new ShoppingCartDto().setId(ID)
                .setCartItems(Set.of(createCartItemDto())).setUserId(ID);
    }
}
