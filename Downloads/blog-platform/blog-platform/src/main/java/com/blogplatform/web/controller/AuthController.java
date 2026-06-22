package com.blogplatform.web.controller;

import com.blogplatform.exception.DuplicateUserException;
import com.blogplatform.service.UserService;
import com.blogplatform.web.dto.RegisterFormDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterFormDto());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterFormDto registerForm,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.register(registerForm);
        } catch (DuplicateUserException ex) {
            bindingResult.reject("registration.error", ex.getMessage());
            return "register";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Registration successful. Please log in.");
        return "redirect:/login";
    }
}
