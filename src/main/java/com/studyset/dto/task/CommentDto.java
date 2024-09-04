package com.studyset.dto.task;

import lombok.Builder;

public class CommentDto {
    private Long userId;
    private Long submission_id;
    private String contents;
    private boolean anonymous;

    @Builder
    public CommentDto(Long userId, Long submission_id, String contents, boolean anonymous) {
        this.userId = userId;
        this.submission_id = submission_id;
        this.contents = contents;
        this.anonymous = anonymous;
    }
}
