package com.friendsbook.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.friendsbook.DTO.CommentsDTO;
import com.friendsbook.entity.Comments;
import com.friendsbook.entity.Posts;
import com.friendsbook.entity.Users;
import com.friendsbook.repository.CommentRepository;
import com.friendsbook.repository.PostRepository;
import com.friendsbook.repository.UserRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public String addComment(Long postId, String userId, String commentContent) {
        Posts post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Comments comment = new Comments();
        comment.setPost(post);
        comment.setUser(user);
        comment.setCommentContent(commentContent);
        commentRepository.save(comment);
        return "Comment add successfully";
    }

    public List<CommentsDTO> getCommentsByPost(Long postId) {
        Posts post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        List<Comments> comments = commentRepository.findAllByPost(post);
        return comments.stream().map(comment -> new CommentsDTO(comment.getCommentId(), comment.getCommentContent(),
                comment.getUser().getUserId()
        )).collect(Collectors.toList());
    }
}
