package com.studyset.domain;

import com.studyset.dto.task.CommentDto;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter
public class Comment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private TaskSubmission taskSubmission;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private boolean anonymous;

    public CommentDto toDto() {
        return CommentDto.builder()
                .userId(user.getId())
                .submission_id(taskSubmission.getId())
                .contents(contents)
                .anonymous(anonymous)
                .build();
    }
}
