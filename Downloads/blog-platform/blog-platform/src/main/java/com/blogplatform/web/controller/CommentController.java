package com.blogplatform.web.controller;

import com.blogplatform.model.entity.User;
import com.blogplatform.service.CommentService;
import com.blogplatform.service.UserService;
import com.blogplatform.web.dto.CommentFormDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/posts/{postId}")
    public String addComment(@PathVariable UUID postId,
                             @Valid @ModelAttribute("commentForm") CommentFormDto commentForm,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.commentForm", bindingResult);
            redirectAttributes.addFlashAttribute("commentForm", commentForm);
            return "redirect:/posts/" + postId;
        }

        User author = userService.findByUsername(userDetails.getUsername());
        commentService.create(postId, commentForm, author);
        redirectAttributes.addFlashAttribute("successMessage", "Comment added successfully.");
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable UUID commentId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        UUID postId = commentService.findById(commentId).getPost().getId();
        commentService.delete(commentId, currentUser, isAdmin);
        redirectAttributes.addFlashAttribute("successMessage", "Comment deleted successfully.");
        return "redirect:/posts/" + postId;
    }
}
