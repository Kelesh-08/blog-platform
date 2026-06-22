package com.blogplatform.service;

import com.blogplatform.exception.CategoryNotFoundException;
import com.blogplatform.exception.DuplicateCategoryException;
import com.blogplatform.model.entity.Category;
import com.blogplatform.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));
    }

    @Transactional
    public Category create(String name, String description) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateCategoryException("Category with this name already exists.");
        }

        Category category = new Category(UUID.randomUUID(), name, description);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(UUID id, String name, String description) {
        Category category = findById(id);

        categoryRepository.findByNameIgnoreCase(name)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateCategoryException("Category with this name already exists.");
                });

        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(UUID id) {
        Category category = findById(id);
        categoryRepository.delete(category);
    }
}
