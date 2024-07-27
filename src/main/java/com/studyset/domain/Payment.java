package com.studyset.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/*사용 내역*/
@Entity
public class Payment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    private LocalDateTime paymentDate;
    @Column(nullable = false)
    private Double price;
    private String description;
}
