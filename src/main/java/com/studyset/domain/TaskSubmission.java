package com.studyset.domain;

import com.studyset.dto.task.TaskSubmissionDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
public class TaskSubmission extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private String contents;
    private String filePath;

    public TaskSubmissionDto toDto() {
        return TaskSubmissionDto.builder()
                .id(id)
                .taskId(task.getId())
                .userId(user.getId())
                .contents(contents)
                .filePath(filePath)
                .updatedDate(getUpdatedDate().toLocalDate())  // BaseEntity로부터 상속받은 필드
                .build();
    }
}
