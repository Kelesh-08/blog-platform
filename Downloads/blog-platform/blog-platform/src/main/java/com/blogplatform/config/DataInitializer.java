package com.blogplatform.config;

import com.blogplatform.model.entity.Category;
import com.blogplatform.model.entity.Role;
import com.blogplatform.model.entity.User;
import com.blogplatform.repository.CategoryRepository;
import com.blogplatform.repository.RoleRepository;
import com.blogplatform.repository.UserRepository;
import com.blogplatform.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(RoleRepository roleRepository,
                               UserRepository userRepository,
                               CategoryRepository categoryRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            Role adminRole = roleRepository.findByName(UserService.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(UUID.randomUUID(), UserService.ROLE_ADMIN)));
            Role authorRole = roleRepository.findByName(UserService.ROLE_AUTHOR)
                    .orElseGet(() -> roleRepository.save(new Role(UUID.randomUUID(), UserService.ROLE_AUTHOR)));
            roleRepository.findByName(UserService.ROLE_READER)
                    .orElseGet(() -> roleRepository.save(new Role(UUID.randomUUID(), UserService.ROLE_READER)));

            if (!userRepository.existsByUsername("admin")) {
                userRepository.save(new User(
                        UUID.randomUUID(),
                        "admin",
                        passwordEncoder.encode("admin123"),
                        "admin@blog.local",
                        adminRole
                ));
            }

            if (!userRepository.existsByUsername("author")) {
                userRepository.save(new User(
                        UUID.randomUUID(),
                        "author",
                        passwordEncoder.encode("author123"),
                        "author@blog.local",
                        authorRole
                ));
            }

            if (categoryRepository.count() == 0) {
                categoryRepository.save(new Category(UUID.randomUUID(), "Technology", "Posts about software and gadgets"));
                categoryRepository.save(new Category(UUID.randomUUID(), "Travel", "Stories and tips from around the world"));
                categoryRepository.save(new Category(UUID.randomUUID(), "Lifestyle", "Everyday inspiration and personal growth"));
            }
        };
    }
}
