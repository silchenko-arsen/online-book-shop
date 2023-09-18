package com.example.service.impl;

import com.example.dto.cartitem.CartItemDto;
import com.example.dto.cartitem.CartItemQuantityDto;
import com.example.dto.cartitem.CartItemRequestDto;
import com.example.dto.shoppingcart.ShoppingCartDto;
import com.example.exception.EntityNotFoundException;
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
import com.example.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto getShoppingCartByUserEmail(String email) {
        User user = getUserByEmail(email);
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByUserId(user.getId())
                .orElseGet(() -> registerNewShoppingCart(user));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public CartItemDto addCartItem(String email, CartItemRequestDto cartItemRequestDto) {
        User user = getUserByEmail(email);
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByUserId(user.getId())
                .orElseGet(() -> registerNewShoppingCart(user));
        Optional<CartItem> existingCartItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(cartItemRequestDto.getBookId()))
                .findFirst();
        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItemRequestDto.getQuantity());
        } else {
            cartItem = createNewCartItem(cartItemRequestDto, shoppingCart);
        }
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public CartItemDto updateBookQuantity(Long cartItemId, CartItemQuantityDto cartItemQuantityDto) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find cart item "
                        + "by cart item id " + cartItemId));
        cartItem.setQuantity(cartItemQuantityDto.getQuantity());
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void deleteCartItemById(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Can not find user by email" + email));
    }

    private ShoppingCart registerNewShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        return shoppingCartRepository.save(shoppingCart);
    }

    private CartItem createNewCartItem(CartItemRequestDto cartItemRequestDto,
                                       ShoppingCart shoppingCart) {
        Book book = bookRepository
                .findById(cartItemRequestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can not find book with id: "
                                + cartItemRequestDto.getBookId()));
        CartItem cartItem = cartItemMapper.toEntity(cartItemRequestDto);
        cartItem.setBook(book);
        cartItem.setShoppingCart(shoppingCart);
        return cartItem;
    }
}
