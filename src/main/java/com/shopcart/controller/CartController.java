package com.shopcart.controller;

import com.shopcart.dto.CartRequest;
import com.shopcart.entity.CartItem;
import com.shopcart.entity.User;
import com.shopcart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getCart(user));
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addItem(@AuthenticationPrincipal User user,
                                            @Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.addItem(user, request.getProductId(), request.getQuantity()));
    }

    @PutMapping("/update/{itemId}")
    public ResponseEntity<CartItem> updateItem(@AuthenticationPrincipal User user,
                                               @PathVariable Long itemId,
                                               @Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.updateItem(user, itemId, request.getQuantity()));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Map<String, String>> removeItem(@AuthenticationPrincipal User user,
                                                          @PathVariable Long itemId) {
        cartService.removeItem(user, itemId);
        return ResponseEntity.ok(Map.of("message", "Item removed from cart"));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.ok(Map.of("message", "Cart cleared"));
    }
}
