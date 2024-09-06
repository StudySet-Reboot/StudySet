package com.studyset.service;

import com.studyset.api.exception.TaskNotExist;
import com.studyset.api.exception.UserNotExist;
import com.studyset.domain.Comment;
import com.studyset.domain.TaskSubmission;
import com.studyset.domain.User;
import com.studyset.dto.task.CommentDto;
import com.studyset.repository.CommentRepository;
import com.studyset.repository.TaskSubmissionRepository;
import com.studyset.repository.UserRepository;
import com.studyset.web.form.CommentForm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskSubmissionRepository taskSubmissionRepository;

    // 댓글 조회
    public List<CommentDto> getCommentBySubmissionId(Long submissionId) {
        List<Comment> commentList = commentRepository.findByTaskSubmission_Id(submissionId);
        return commentList.stream().map(Comment::toDto).collect(Collectors.toList());
    }

    // 댓글 작성
    @Transactional
    public CommentDto addComment(CommentForm commentForm) {
        User user = userRepository.findById(commentForm.getUserId())
                .orElseThrow(() -> new UserNotExist());

        TaskSubmission taskSubmission = taskSubmissionRepository.findById(commentForm.getSubmission_id())
                .orElseThrow(() -> new TaskNotExist());

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setTaskSubmission(taskSubmission);
        comment.setContents(commentForm.getContents());
        comment.setAnonymous(commentForm.isAnonymous());

        commentRepository.save(comment);

        return comment.toDto();
    }

    // 댓글 삭제


}
