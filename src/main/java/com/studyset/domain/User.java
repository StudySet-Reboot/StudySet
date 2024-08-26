package com.studyset.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@DynamicUpdate
@Getter @Setter
@NoArgsConstructor
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(nullable = false)
    private String name;
    private String provider;
    private String phone;
    @Column(nullable = false, unique = true)
    private String email;
    // private LocalDate birth;

    @Builder //생성을 Builder 패턴으로 하기 위해서
    public User(Long id, String name, String email, String provider, String phone) {
        this.id = id;
        this.name = name;
        this.provider = provider;
        this.phone = phone;
        this.email = email;
    }

    public User update(String name, String email) {
        this.name = name;
        this.email = email;
        return this;
    }
}
