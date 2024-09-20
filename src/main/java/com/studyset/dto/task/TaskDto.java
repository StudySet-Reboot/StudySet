package com.studyset.dto.task;

import com.studyset.domain.Group;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class TaskDto {
    private Long id;
    private Group group;
    private String taskName;
    private String description;
    private LocalDate startTime;
    private LocalDate endTime;
    private String startTimeFormatted;
    private String endTimeFormatted;

    @Builder
    public TaskDto(Long id, Group group, String taskName, String description, LocalDate startTime, LocalDate endTime) {
        this.id = id;
        this.group = group;
        this.taskName = taskName;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TaskDto(String taskName) {
        this.taskName = taskName;
    }
}
