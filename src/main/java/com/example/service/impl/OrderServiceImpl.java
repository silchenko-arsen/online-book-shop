package com.example.service.impl;

import com.example.dto.order.OrderDto;
import com.example.dto.order.OrderShippingAddressDto;
import com.example.dto.order.OrderStatusDto;
import com.example.dto.orderitem.OrderItemResponseDto;
import com.example.exception.EntityNotFoundException;
import com.example.mapper.OrderItemMapper;
import com.example.mapper.OrderMapper;
import com.example.model.CartItem;
import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.model.ShoppingCart;
import com.example.model.User;
import com.example.repository.OrderItemRepository;
import com.example.repository.OrderRepository;
import com.example.repository.ShoppingCartRepository;
import com.example.repository.UserRepository;
import com.example.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderDto save(String email, OrderShippingAddressDto shippingAddress) {
        User user = getUserByEmail(email);
        Order order = createOrder(user, shippingAddress);
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find shopping cart "
                        + "by user id " + user.getId()));
        List<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(cartItem -> mapToOrderItem(cartItem, order))
                .toList();
        shoppingCartRepository.delete(shoppingCart);
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getAllOrders(String email, Pageable pageable) {
        User user = getUserByEmail(email);
        Page<Order> allOrders = orderRepository.findAllByUserId(user.getId(), pageable);
        return allOrders.stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderItemResponseDto> getAllOrderItems(Long orderId, Pageable pageable) {
        Order order = findByIdWithOrderItems(orderId);
        return order.getOrderItems().stream()
                .map(orderItemMapper::toResponseDto)
                .toList();
    }

    @Override
    public OrderItemResponseDto getOrderItem(Long orderId, Long itemId) {
        List<OrderItemResponseDto> allOrderItems = getAllOrderItems(orderId, Pageable.unpaged());
        return allOrderItems.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Can't find order item by id "
                        + itemId));
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, OrderStatusDto statusDto) {
        Order order = findByIdWithOrderItems(orderId);
        order.setStatus(statusDto.getStatus());
        return orderMapper.toDto(orderRepository.save(order));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Can not find user by email" + email));
    }

    private OrderItem mapToOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setBook(cartItem.getBook());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getBook().getPrice());
        orderItem.setOrder(order);
        return orderItem;
    }

    private Order findByIdWithOrderItems(Long id) {
        return orderRepository.findByIdWithOrderItems(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find order by id " + id));
    }

    private Order createOrder(User user,
                              OrderShippingAddressDto shippingAddress) {
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find shopping cart "
                        + "by user id " + user.getId()));
        double total = shoppingCart.getCartItems().stream()
                .mapToDouble(cartItem -> (double) cartItem.getQuantity()
                        * cartItem.getBook().getPrice().doubleValue())
                .sum();
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress.getShippingAddress());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.NEW);
        order.setTotal(BigDecimal.valueOf(total));
        return orderRepository.save(order);
    }
}
