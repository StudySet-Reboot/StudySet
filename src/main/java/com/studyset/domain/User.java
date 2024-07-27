package com.studyset.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(nullable = false)
    private String name;
    private String phone;
    @Column(nullable = false, unique = true)
    private String email;
    private LocalDateTime birth;
}
