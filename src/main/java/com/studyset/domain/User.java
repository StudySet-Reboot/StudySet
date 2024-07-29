package com.studyset.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
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

    @Builder
    public User(String name, String phone, String email, LocalDateTime birth) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.birth = birth;
    }
}
