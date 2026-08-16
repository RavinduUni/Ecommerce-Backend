package com.ecom.service;

import com.ecom.dto.CartItem;
import com.ecom.dto.Order;
import com.ecom.dto.User;
import com.ecom.entity.CartItemEntity;
import com.ecom.entity.OrderEntity;
import com.ecom.entity.OrderItemEntity;
import com.ecom.entity.UserEntity;
import com.ecom.enums.OrderStatus;
import com.ecom.mapper.CartItemMapper;
import com.ecom.mapper.OrderMapper;
import com.ecom.repository.OrderRepository;
import com.ecom.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final CartItemMapper cartItemMapper;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(CartService cartService, UserRepository userRepository, CartItemMapper cartItemMapper, OrderRepository orderRepository, OrderMapper orderMapper) {
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.cartItemMapper = cartItemMapper;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }
    @Transactional
    public Optional<Order> createOrder(String userId) {
        List<CartItemEntity> cartItemEntities = cartService.getCart(userId).stream().map(cartItemMapper::toCartItemEntity).toList();

        if (cartItemEntities.isEmpty()){
            return Optional.empty();
        }

        Optional<UserEntity> userOptional = userRepository.findById(Long.valueOf(userId));

        if (userOptional.isEmpty()){
            return Optional.empty();
        }

        UserEntity userEntity = userOptional.get();

        BigDecimal totalPrice = cartItemEntities.stream().map(CartItemEntity::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUser(userEntity);
        orderEntity.setStatus(OrderStatus.CONFIRMED);
        orderEntity.setTotalAmount(totalPrice);

        List<OrderItemEntity> orderItemEntities = cartItemEntities.stream().map(cartItemEntity ->  new OrderItemEntity(
                null,
                cartItemEntity.getProduct(),
                cartItemEntity.getQuantity(),
                cartItemEntity.getPrice(),
                orderEntity
        )).toList();

        orderEntity.setItems(orderItemEntities);

        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);
        //clear the cart
        cartService.clearCart(userId);

        return Optional.ofNullable(orderMapper.toOrderDto(savedOrderEntity));

    }
}
