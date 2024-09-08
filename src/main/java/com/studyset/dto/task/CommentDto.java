package com.studyset.dto.task;

import lombok.Builder;
import lombok.Data;

@Data
public class CommentDto {
    private Long user_id;
    private Long submission_id;
    private String contents;
    private boolean anonymous;
    private String updatedDate; // 날짜만 포함
    private String userName;

    @Builder
    public CommentDto(Long user_id, Long submission_id, String contents, boolean anonymous, String updatedDate) {
        this.user_id = user_id;
        this.submission_id = submission_id;
        this.contents = contents;
        this.anonymous = anonymous;
        this.updatedDate = updatedDate;
    }
}
