package com.studyset.api.request.schedule;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleEditRequest {
    private String title;
    private Boolean isImportant;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String location;
}
