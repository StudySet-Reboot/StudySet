package com.studyset.dto.task;

import com.studyset.domain.Group;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class TaskDto {
    private Long id;
    private Long groupId;
    private String taskName;
    private String description;
    private LocalDate startTime;
    private LocalDate endTime;
    private String taskStatus;
    private String startTimeFormatted;
    private String endTimeFormatted;

    @Builder
    public TaskDto(Long id, Long groupId, String taskName, String description, LocalDate startTime, LocalDate endTime, String taskStatus, String startTimeFormatted, String endTimeFormatted) {
        this.id = id;
        this.groupId = groupId;
        this.taskName = taskName;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.taskStatus = taskStatus;
        this.startTimeFormatted = startTimeFormatted;
        this.endTimeFormatted = endTimeFormatted;
    }

    public TaskDto(String taskName) {
        this.taskName = taskName;
    }
}
