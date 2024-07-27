package com.studyset.domain;

import jakarta.persistence.*;
import org.hibernate.mapping.Join;

@Entity
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
}
