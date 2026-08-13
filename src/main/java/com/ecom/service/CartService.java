package com.ecom.service;

import com.ecom.dto.CartItem;
import com.ecom.entity.CartItemEntity;
import com.ecom.entity.ProductEntity;
import com.ecom.entity.UserEntity;
import com.ecom.mapper.CartItemMapper;
import com.ecom.repository.CartItemRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartService {

    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartItemMapper cartItemMapper, CartItemRepository cartItemRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartItemMapper = cartItemMapper;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public String addToCart(String userId, CartItem cartItem) {
        // 1. cartItem.getId() wenuwata getProduct().getId() pawichchi karanna
        Long productId = cartItem.getProduct().getId();
        Optional<ProductEntity> productOpt = productRepository.findById(productId);

        if (productOpt.isEmpty()){
            return "Product not found";
        }
        ProductEntity productEntity = productOpt.get();

        if (productEntity.getStockQuantity() < cartItem.getQuantity()){
            return "Stock Limit exceed";
        }

        Optional<UserEntity> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()){
            return "User not found";
        }
        UserEntity userEntity = userOpt.get();

        CartItemEntity existingCartItem = cartItemRepository.findByUserAndProduct(userEntity, productEntity);

        if (existingCartItem != null){
            // Update quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItem.getQuantity());

            // Update price (Product price * new total quantity)
            existingCartItem.setPrice(productEntity.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));

            cartItemRepository.save(existingCartItem);
        } else {
            // Create new cartItem
            CartItemEntity cartItemEntity = new CartItemEntity();

            // 2. Mapper eka use karana ekata wada, me fields tika manual set karana eka safe.
            // Mokada API request eken User saha Price ewanne nathi nisa.
            cartItemEntity.setUser(userEntity);
            cartItemEntity.setProduct(productEntity);
            cartItemEntity.setQuantity(cartItem.getQuantity());

            // Calculate price for the new item
            cartItemEntity.setPrice(productEntity.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            cartItemRepository.save(cartItemEntity);
        }

        return "Item added successfully";
    }

    public String removeFromCart(String userId, Long productId){
        Optional<UserEntity> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()){
            return "User not found";
        }
        UserEntity userEntity = userOpt.get();

        Optional<ProductEntity> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()){
            return "Product not found";
        }
        ProductEntity productEntity = productOpt.get();

        CartItemEntity existingCartItem = cartItemRepository.findByUserAndProduct(userEntity, productEntity);
        if (existingCartItem != null){
            cartItemRepository.delete(existingCartItem);
            return "Item removed successfully";
        }else {
            return "Item not found in cart";
        }
    }
}
