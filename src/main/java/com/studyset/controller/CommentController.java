package com.studyset.controller;

import com.studyset.api.exception.UserNotExist;
import com.studyset.domain.User;
import com.studyset.dto.memo.MemoDto;
import com.studyset.dto.task.CommentDto;
import com.studyset.repository.UserRepository;
import com.studyset.service.CommentService;
import com.studyset.web.form.CommentForm;
import com.studyset.web.form.MemoCreateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller()
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;
    private final UserRepository userRepository;

    // 댓글 작성
    @PostMapping("/userTask/addComment")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addComment(@RequestBody CommentForm comment) {
        CommentDto newComment = commentService.addComment(comment);

        Map<String, Object> response = new HashMap<>();
        response.put("newComment", newComment);
        return ResponseEntity.ok(response);
    }

    // 댓글 삭제

}
