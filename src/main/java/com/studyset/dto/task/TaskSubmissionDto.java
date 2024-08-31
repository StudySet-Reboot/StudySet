package com.studyset.dto.task;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
public class TaskSubmissionDto {
    private Long id;
    private Long taskId;
    private Long userId;
    private String contents;
    private String filePath;
    private LocalDate updatedDate; // 날짜만 포함

    @Builder
    public TaskSubmissionDto(Long id, Long taskId, Long userId, String contents, String filePath, LocalDate updatedDate) {
        this.id = id;
        this.taskId = taskId;
        this.userId = userId;
        this.contents = contents;
        this.filePath = filePath;
        this.updatedDate = updatedDate;
    }
}
