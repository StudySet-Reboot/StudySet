package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.task.CommentDto;
import com.studyset.repository.UserRepository;
import com.studyset.service.CommentService;
import com.studyset.web.form.CommentForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;
    private final UserRepository userRepository;

    // 댓글 작성
    @PostMapping("/userTask/addComment")
    public ResponseEntity<Map<String, Object>> addComment(@RequestBody CommentForm comment) {
        CommentDto newComment = commentService.addComment(comment);
        // 새로운 유저의 이름을 포함해 응답
        Optional<User> user = userRepository.findById(newComment.getUser_id());
        newComment.setUserName(user.get().getName());

        Map<String, Object> response = new HashMap<>();
        response.put("newComment", newComment);
        return ResponseEntity.ok(response);
    }

    // 댓글 삭제
    @DeleteMapping("/userTask/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        log.info("Deleting comment: {}", commentId);
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
