package com.studyset.dto.task;

import com.studyset.domain.Task;
import com.studyset.domain.User;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TaskSubmissionDto {
    private Long id;
    private Task task;
    private User user;
    private String contents;
    private String filePath;

    @Builder
    public TaskSubmissionDto(Long id, Task task, User user, String contents, String filePath) {
        this.id = id;
        this.task = task;
        this.user = user;
        this.contents = contents;
        this.filePath = filePath;
    }
}
