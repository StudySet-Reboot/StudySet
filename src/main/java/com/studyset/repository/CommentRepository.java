package com.studyset.repository;

import com.studyset.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 과제 내 모든 댓글 조회
    List<Comment> findByTaskSubmission_Id(@Param("submission_id") Long submissionId);

    // 과제 삭제 전 댓글 일괄 삭제
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.taskSubmission.id = :submissionId")
    void deleteBySubmissionId(@Param("submissionId") Long submissionId);
}
