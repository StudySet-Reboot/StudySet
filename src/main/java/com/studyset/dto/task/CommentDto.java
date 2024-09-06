package com.studyset.dto.task;

import com.studyset.domain.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CommentDto {
    private User user;
    private Long submission_id;
    private String contents;
    private boolean anonymous;
    private String updatedDate; // 날짜만 포함

    @Builder
    public CommentDto(User user, Long submission_id, String contents, boolean anonymous, String updatedDate) {
        this.user = user;
        this.submission_id = submission_id;
        this.contents = contents;
        this.anonymous = anonymous;
        this.updatedDate = updatedDate;
    }
}
