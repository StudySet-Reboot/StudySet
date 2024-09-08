package com.studyset.web.form;

import lombok.Data;

@Data
public class CommentForm {
    private Long userId;
    private Long submission_id;
    private String contents;
    private boolean anonymous;
}
