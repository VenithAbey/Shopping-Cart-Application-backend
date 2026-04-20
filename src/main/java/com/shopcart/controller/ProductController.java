package com.shopcart.controller;

import com.shopcart.entity.Product;
import com.shopcart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAll(
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(productService.getAll(categoryName, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Product> create(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(productService.create(
                (String) body.get("name"),
                (String) body.get("description"),
                new BigDecimal(body.get("price").toString()),
                (Integer) body.get("stock"),
                (String) body.get("imageUrl"),
                Long.valueOf(body.get("categoryId").toString())
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        BigDecimal price = body.get("price") != null ? new BigDecimal(body.get("price").toString()) : null;
        Long categoryId = body.get("categoryId") != null ? Long.valueOf(body.get("categoryId").toString()) : null;

        return ResponseEntity.ok(productService.update(
                id,
                (String) body.get("name"),
                (String) body.get("description"),
                price,
                (Integer) body.get("stock"),
                (String) body.get("imageUrl"),
                categoryId
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Product deleted"));
    }
}
