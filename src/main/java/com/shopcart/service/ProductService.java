package com.shopcart.service;

import com.shopcart.entity.Category;
import com.shopcart.entity.Product;
import com.shopcart.repository.CategoryRepository;
import com.shopcart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<Product> getAll(String categoryName, String search) {
        String cat = (categoryName == null || categoryName.isBlank()) ? null : categoryName;
        String searchTerm = (search == null || search.isBlank()) ? null : search;
        if (cat == null && searchTerm == null) {
            return productRepository.findAll();
        }
        return productRepository.findByCategoryNameAndSearch(cat, searchTerm);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product create(String name, String description, BigDecimal price, Integer stock,
                          String imageUrl, String subcategory, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .imageUrl(imageUrl)
                .subcategory(subcategory)
                .category(category)
                .build();

        return productRepository.save(product);
    }

    public Product update(Long id, String name, String description, BigDecimal price,
                          Integer stock, String imageUrl, String subcategory, Long categoryId) {
        Product product = getById(id);

        if (name != null) product.setName(name);
        if (description != null) product.setDescription(description);
        if (price != null) product.setPrice(price);
        if (stock != null) product.setStock(stock);
        if (imageUrl != null) product.setImageUrl(imageUrl);
        if (subcategory != null) product.setSubcategory(subcategory);
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
