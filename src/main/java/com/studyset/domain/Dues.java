package com.studyset.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/*납부 내역*/
@Entity
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
    private LocalDateTime duesDate;
}
