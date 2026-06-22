package com.blogplatform.service;

import com.blogplatform.exception.DuplicateUserException;
import com.blogplatform.model.entity.Role;
import com.blogplatform.model.entity.User;
import com.blogplatform.repository.RoleRepository;
import com.blogplatform.repository.UserRepository;
import com.blogplatform.web.dto.RegisterFormDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_AUTHOR = "AUTHOR";
    public static final String ROLE_READER = "READER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().getName())
                .build();
    }

    @Transactional
    public User register(RegisterFormDto formDto) {
        if (userRepository.existsByUsername(formDto.getUsername())) {
            throw new DuplicateUserException("Username is already taken.");
        }
        if (userRepository.existsByEmail(formDto.getEmail())) {
            throw new DuplicateUserException("Email is already registered.");
        }

        Role readerRole = roleRepository.findByName(ROLE_READER)
                .orElseThrow(() -> new IllegalStateException("Reader role is missing."));

        User user = new User(
                UUID.randomUUID(),
                formDto.getUsername(),
                passwordEncoder.encode(formDto.getPassword()),
                formDto.getEmail(),
                readerRole
        );

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
