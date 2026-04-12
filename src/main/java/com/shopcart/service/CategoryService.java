package com.shopcart.service;

import com.shopcart.entity.Category;
import com.shopcart.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category create(String name, String description) {
        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException("Category already exists");
        }
        return categoryRepository.save(Category.builder()
                .name(name)
                .description(description)
                .build());
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
