package com.blogplatform.repository;

import com.blogplatform.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByCategoryIdOrderByCreatedAtDesc(UUID categoryId);
}
