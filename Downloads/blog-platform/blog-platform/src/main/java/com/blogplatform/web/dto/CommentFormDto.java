package com.blogplatform.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentFormDto {

    @NotBlank(message = "Comment is required.")
    @Size(min = 2, max = 500, message = "Comment must be between 2 and 500 characters.")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
