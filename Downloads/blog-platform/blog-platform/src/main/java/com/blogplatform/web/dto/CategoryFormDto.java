package com.blogplatform.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryFormDto {

    @NotBlank(message = "Name is required.")
    @Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters.")
    private String name;

    @NotBlank(message = "Description is required.")
    @Size(min = 5, max = 255, message = "Description must be between 5 and 255 characters.")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
