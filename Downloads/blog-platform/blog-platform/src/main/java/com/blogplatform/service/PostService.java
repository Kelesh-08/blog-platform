package com.blogplatform.service;

import com.blogplatform.exception.PostNotFoundException;
import com.blogplatform.exception.UnauthorizedActionException;
import com.blogplatform.model.entity.Category;
import com.blogplatform.model.entity.Post;
import com.blogplatform.model.entity.User;
import com.blogplatform.repository.PostRepository;
import com.blogplatform.web.dto.PostFormDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;

    public PostService(PostRepository postRepository, CategoryService categoryService) {
        this.postRepository = postRepository;
        this.categoryService = categoryService;
    }

    public List<Post> findAll() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> findByCategory(UUID categoryId) {
        return postRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId);
    }

    public Post findById(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found."));
    }

    @Transactional
    public Post create(PostFormDto formDto, User author) {
        Category category = categoryService.findById(formDto.getCategoryId());

        Post post = new Post(
                UUID.randomUUID(),
                formDto.getTitle(),
                formDto.getContent(),
                LocalDateTime.now(),
                author,
                category
        );

        return postRepository.save(post);
    }

    @Transactional
    public Post update(UUID postId, PostFormDto formDto, User currentUser, boolean isAdmin) {
        Post post = findById(postId);
        ensureCanModify(post, currentUser, isAdmin);

        Category category = categoryService.findById(formDto.getCategoryId());
        post.setTitle(formDto.getTitle());
        post.setContent(formDto.getContent());
        post.setCategory(category);

        return postRepository.save(post);
    }

    @Transactional
    public void delete(UUID postId, User currentUser, boolean isAdmin) {
        Post post = findById(postId);
        ensureCanModify(post, currentUser, isAdmin);
        postRepository.delete(post);
    }

    private void ensureCanModify(Post post, User currentUser, boolean isAdmin) {
        if (isAdmin) {
            return;
        }
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You are not allowed to modify this post.");
        }
    }
}
