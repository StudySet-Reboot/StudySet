package com.studyset.web.form;

import lombok.Data;

@Data
public class TaskSubmissionForm {
    private Long taskId;
    private Long userId;
    private String content;
    private String filePath;
}
