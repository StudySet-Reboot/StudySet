package com.studyset.dto.schedule;
import lombok.Builder;
import lombok.Getter;

import java.awt.*;
import java.time.LocalDateTime;

@Getter
public class Event {
    public static final String IMPORTANT_COLOR = "PINK";
    public static final String NORMAL_COLOR = "GRAY";

    private long id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String description;
    private String color;
    private String location;

    @Builder
    public Event(long id, String title, LocalDateTime start, LocalDateTime end, String description, String color, String location) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.description = description;
        this.color = color;
        this.location = location;
    }
}
