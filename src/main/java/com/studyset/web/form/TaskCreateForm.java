package com.studyset.web.form;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskCreateForm {
    private String taskName;
    private Long groupId;
    private String description;
    private LocalDate startTime;
    private LocalDate endTime;
}
