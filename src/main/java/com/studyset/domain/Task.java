package com.studyset.domain;

import com.studyset.dto.task.TaskDto;
import jakarta.persistence.*;
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
