package com.studyset.domain;

import com.studyset.web.form.DuesForm;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*납부 내역*/
@Entity
@Getter
@NoArgsConstructor
public class Dues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dues_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private Double price;
    private LocalDate duesDate;

    @Builder
    public Dues(Long id, Group group, User user, Double price, LocalDate duesDate) {
        this.id = id;
        this.group = group;
        this.user = user;
        this.price = price;
        this.duesDate = duesDate;
    }
}
