package com.studyset.domain;

import jakarta.persistence.*;

@Entity
public class TimeSlot{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_slot_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false)
    private int dayIdx; //요일
    @Column(nullable = false)
    private int startTime;
    @Column(nullable = false)
    private int endTime;
}
