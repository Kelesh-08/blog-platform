package com.blogplatform.web.controller;

import com.blogplatform.exception.DuplicateCategoryException;
import com.blogplatform.service.CategoryService;
import com.blogplatform.web.dto.CategoryFormDto;
import com.blogplatform.web.dto.CategoryEditFormDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        if (!model.containsAttribute("categoryForm")) {
            model.addAttribute("categoryForm", new CategoryFormDto());
        }
        return "categories";
    }

    @PostMapping
    public String createCategory(@Valid @ModelAttribute("categoryForm") CategoryFormDto categoryForm,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.categoryForm", bindingResult);
            redirectAttributes.addFlashAttribute("categoryForm", categoryForm);
            return "redirect:/categories";
        }

        try {
            categoryService.create(categoryForm.getName(), categoryForm.getDescription());
        } catch (DuplicateCategoryException ex) {
            bindingResult.reject("category.error", ex.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.categoryForm", bindingResult);
            redirectAttributes.addFlashAttribute("categoryForm", categoryForm);
            return "redirect:/categories";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Category created successfully.");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/edit")
    public String updateCategory(@PathVariable UUID id,
                                 @Valid @ModelAttribute("editForm") CategoryEditFormDto editForm,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editForm_" + id, bindingResult);
            redirectAttributes.addFlashAttribute("editForm_" + id, editForm);
            redirectAttributes.addFlashAttribute("editErrorId", id);
            redirectAttributes.addFlashAttribute("editErrorMessage",
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/categories";
        }

        try {
            categoryService.update(id, editForm.getName().trim(), editForm.getDescription().trim());
        } catch (DuplicateCategoryException ex) {
            redirectAttributes.addFlashAttribute("editErrorId", id);
            redirectAttributes.addFlashAttribute("editErrorMessage", ex.getMessage());
            return "redirect:/categories";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully.");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully.");
        return "redirect:/categories";
    }
}
