package com.studyset.repository;

import com.studyset.domain.Comment;
import com.studyset.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 과제 내 모든 댓글 조회
    List<Comment> findByTaskSubmission_Id(@Param("submission_id") Long submissionId);

    // 과제 내 특정 댓글 삭제
    @Query("SELECT c FROM Comment c WHERE c.taskSubmission.id = :submission_id AND c.user.id = :userId")
    Comment findByTaskSubmission_IdAndUserId(@Param("submission_id") Long submission_id, @Param("userId") Long userId);
}
