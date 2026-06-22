package com.blogplatform.web.controller;

import com.blogplatform.model.entity.User;
import com.blogplatform.service.CategoryService;
import com.blogplatform.service.CommentService;
import com.blogplatform.service.PostService;
import com.blogplatform.service.UserService;
import com.blogplatform.web.dto.CommentFormDto;
import com.blogplatform.web.dto.PostFormDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final CategoryService categoryService;
    private final UserService userService;

    public PostController(PostService postService,
                          CommentService commentService,
                          CategoryService categoryService,
                          UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping
    public String listPosts(@RequestParam(required = false) UUID categoryId, Model model) {
        model.addAttribute("posts", categoryId == null
                ? postService.findAll()
                : postService.findByCategory(categoryId));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("selectedCategoryId", categoryId);
        return "posts";
    }

    @GetMapping("/{id}")
    public String postDetails(@PathVariable UUID id, Model model) {
        model.addAttribute("post", postService.findById(id));
        model.addAttribute("comments", commentService.findByPost(id));
        if (!model.containsAttribute("commentForm")) {
            model.addAttribute("commentForm", new CommentFormDto());
        }
        return "post-detail";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("postForm", new PostFormDto());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("formAction", "/posts/create");
        model.addAttribute("pageTitle", "Create Post");
        return "post-form";
    }

    @PostMapping("/create")
    public String createPost(@Valid @ModelAttribute("postForm") PostFormDto postForm,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("formAction", "/posts/create");
            model.addAttribute("pageTitle", "Create Post");
            return "post-form";
        }

        User author = userService.findByUsername(userDetails.getUsername());
        UUID postId = postService.create(postForm, author).getId();
        redirectAttributes.addFlashAttribute("successMessage", "Post created successfully.");
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var post = postService.findById(id);
        PostFormDto formDto = new PostFormDto();
        formDto.setTitle(post.getTitle());
        formDto.setContent(post.getContent());
        formDto.setCategoryId(post.getCategory().getId());

        model.addAttribute("postForm", formDto);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("formAction", "/posts/" + id + "/edit");
        model.addAttribute("pageTitle", "Edit Post");
        return "post-form";
    }

    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable UUID id,
                             @Valid @ModelAttribute("postForm") PostFormDto postForm,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("formAction", "/posts/" + id + "/edit");
            model.addAttribute("pageTitle", "Edit Post");
            return "post-form";
        }

        User currentUser = userService.findByUsername(userDetails.getUsername());
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        postService.update(id, postForm, currentUser, isAdmin);
        redirectAttributes.addFlashAttribute("successMessage", "Post updated successfully.");
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable UUID id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        postService.delete(id, currentUser, isAdmin);
        redirectAttributes.addFlashAttribute("successMessage", "Post deleted successfully.");
        return "redirect:/posts";
    }
}
