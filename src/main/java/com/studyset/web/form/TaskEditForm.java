package com.studyset.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskEditForm {
    private Long taskId;
    private Long userId;
    private String content;
    private String filePath;
}
