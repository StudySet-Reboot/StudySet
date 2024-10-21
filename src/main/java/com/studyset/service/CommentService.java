package com.studyset.service;

import com.studyset.exception.TaskNotExist;
import com.studyset.exception.UserNotExist;
import com.studyset.domain.Comment;
import com.studyset.domain.TaskSubmission;
import com.studyset.domain.User;
import com.studyset.dto.task.CommentDto;
import com.studyset.repository.CommentRepository;
import com.studyset.repository.TaskSubmissionRepository;
import com.studyset.repository.UserRepository;
import com.studyset.web.form.CommentForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskSubmissionRepository taskSubmissionRepository;

    /**
     * 제출한 과제의 id로 댓글을 조회합니다.
     *
     * @param submissionId 제출한 과제의 id
     * @return List<CommentDto> 과제에 달린 댓글 목록
     */
    public List<CommentDto> getCommentBySubmissionId(Long submissionId) {
        List<Comment> commentList = commentRepository.findByTaskSubmission_Id(submissionId);
        return commentList.stream().map(Comment::toDto).collect(Collectors.toList());
    }

    /**
     * 댓글을 작성합니다.
     *
     * @param commentForm 댓글 작성 폼
     * @return 작성된 댓글 정보
     * @throws UserNotExist 해당 사용자가 존재하지 않을 경우
     * @throws TaskNotExist 해당 과제가 존재하지 않을 경우
     */
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

    /**
     * 작성한 댓글을 삭제합니다.
     *
     * @param commentId 댓글의 id
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

}
