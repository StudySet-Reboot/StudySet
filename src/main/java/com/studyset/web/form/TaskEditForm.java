package com.studyset.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskEditForm {
    private Long id;
    private String taskName;
    private LocalDate startTime;
    private LocalDate endTime;
    private String description;
}
