package com.blogplatform.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class PostFormDto {

    @NotBlank(message = "Title is required.")
    @Size(min = 5, max = 120, message = "Title must be between 5 and 120 characters.")
    private String title;

    @NotBlank(message = "Content is required.")
    @Size(min = 20, max = 5000, message = "Content must be between 20 and 5000 characters.")
    private String content;

    @NotNull(message = "Category is required.")
    private UUID categoryId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
}
