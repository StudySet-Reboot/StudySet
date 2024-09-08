package com.studyset.domain;

import com.studyset.dto.task.CommentDto;
import java.time.format.DateTimeFormatter;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter
public class Comment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");

        return CommentDto.builder()
                .user_id(user.getId())
                .submission_id(taskSubmission.getId())
                .contents(contents)
                .anonymous(anonymous)
                .updatedDate(getUpdatedDate().format(formatter))
                .build();
    }
}
