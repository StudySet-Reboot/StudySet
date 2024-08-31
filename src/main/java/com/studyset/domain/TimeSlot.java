package com.studyset.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;


@NoArgsConstructor
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
    @Column(name = "available_time")
    private String availTime;

    @Builder
    public TimeSlot(Group group, User user) {
        this.group = group;
        this.user = user;
    }

    @Transient
    public boolean[][] getTimeSlots() {
        boolean[][] timeSlots = new boolean[24][7];
        for (int hour=0; hour<24; hour++){
            for(int day=0; day<7; day++) {
                timeSlots[hour][day] = availTime.charAt(hour*7+day) == '1' ? true : false;
            }
        }
        return timeSlots;
    }

    @Transient
    public void setTimeSlots(boolean[][] timeslotList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int hour=0; hour<24; hour++) {
            for (int day = 0; day < 7; day++) {
                stringBuilder.append(timeslotList[hour][day] ? '1' : '0');
            }
        }
        this.availTime = stringBuilder.toString();
    }
}
