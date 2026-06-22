package com.blogplatform.service;

import com.blogplatform.exception.CommentNotFoundException;
import com.blogplatform.exception.UnauthorizedActionException;
import com.blogplatform.model.entity.Comment;
import com.blogplatform.model.entity.Post;
import com.blogplatform.model.entity.User;
import com.blogplatform.repository.CommentRepository;
import com.blogplatform.web.dto.CommentFormDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    public CommentService(CommentRepository commentRepository, PostService postService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
    }

    public List<Comment> findByPost(UUID postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    public Comment findById(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found."));
    }

    @Transactional
    public Comment create(UUID postId, CommentFormDto formDto, User author) {
        Post post = postService.findById(postId);

        Comment comment = new Comment(
                UUID.randomUUID(),
                formDto.getContent(),
                LocalDateTime.now(),
                post,
                author
        );

        return commentRepository.save(comment);
    }

    @Transactional
    public void delete(UUID commentId, User currentUser, boolean isAdmin) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found."));

        if (!isAdmin && !comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You are not allowed to delete this comment.");
        }

        commentRepository.delete(comment);
    }
}
