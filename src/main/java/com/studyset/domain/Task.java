package com.studyset.domain;

import com.studyset.dto.task.TaskDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Task extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    @Column(nullable = false, length = 40)
    private String taskName;
    private LocalDate startTime;
    private LocalDate endTime;
    private String description;

    @Builder
    public Task(Long id, Group group, String taskName, LocalDate startTime, LocalDate endTime, String description) {
        this.id = id;
        this.group = group;
        this.taskName = taskName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    public TaskDto toDto() {
        return TaskDto.builder()
                .id(id)
                .group(group)
                .taskName(taskName)
                .description(description)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
