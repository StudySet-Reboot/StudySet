package com.studyset.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskSubmissionEditForm {
    private Long taskId;
    private Long userId;
    private String content;
    private String filePath;
}
