package com.studyset.domain;

import com.studyset.api.request.schedule.ScheduleEditRequest;
import com.studyset.api.response.schedule.Event;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class Schedule extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    @Column(nullable = false, length = 40)
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String description;
    private Boolean isImportant;

    @Builder
    public Schedule(String title, LocalDateTime startTime, LocalDateTime endTime, String location, String description, Boolean isImportant) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.description = description;
        this.isImportant = isImportant;
    }

    public void addGroup(Group group) {
        this.group = group;
    }

    public Event toEvent(){
        return Event.builder()
                .id(id)
                .title(title)
                .start(startTime)
                .end(endTime)
                .description(description)
                .location(location)
                .color(isImportant ? Event.IMPORTANT_COLOR : Event.NORMAL_COLOR)
                .isImportant(isImportant)
                .build();
    }

    public void edit(ScheduleEditRequest scheduleEditRequest) {
        this.title = scheduleEditRequest.getTitle();
        this.startTime = scheduleEditRequest.getStartDate();
        this.endTime = scheduleEditRequest.getEndDate();
        this.description = scheduleEditRequest.getDescription();
        this.isImportant = scheduleEditRequest.getIsImportant();
        this.location = scheduleEditRequest.getLocation();
    }
}
