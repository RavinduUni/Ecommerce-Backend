package com.ecom.controller;

import com.ecom.dto.CartItem;
import com.ecom.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/")
    public ResponseEntity<String> addToCart(@RequestHeader("X-User-ID") String userId, @RequestBody CartItem cartItem){
        return new ResponseEntity<>(cartService.addToCart(userId, cartItem), HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeFromCart(@RequestHeader("X-User-ID") String userId, @PathVariable Long productId){
        return new ResponseEntity<>(cartService.removeFromCart(userId, productId), HttpStatus.OK);
    }
}
