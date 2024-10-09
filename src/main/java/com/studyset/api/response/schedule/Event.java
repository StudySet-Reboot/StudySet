package com.studyset.api.response.schedule;
import lombok.Builder;
import lombok.Getter;

import java.awt.*;
import java.time.LocalDateTime;

@Getter
public class Event {

    public static final String IMPORTANT_COLOR = "PINK";
    public static final String NORMAL_COLOR = "GRAY";

    private Long id;

    private String title;

    private LocalDateTime start;

    private LocalDateTime end;

    private String description;

    private String color;

    private String location;

    private Boolean isImportant;

    @Builder
    public Event(Long id,
                 String title,
                 LocalDateTime start,
                 LocalDateTime end,
                 String description,
                 String color,
                 String location,
                 Boolean isImportant) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.description = description;
        this.color = color;
        this.location = location;
        this.isImportant = isImportant;
    }
}
