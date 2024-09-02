package com.studyset.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskCreateForm {
    @NotBlank(message = "제목을 입력해주세요")
    private String taskName;
    private Long groupId;
    private String description;
    @NotNull(message = "시작일을 입력해주세요")
    private LocalDate startTime;
    private LocalDate endTime;
}
