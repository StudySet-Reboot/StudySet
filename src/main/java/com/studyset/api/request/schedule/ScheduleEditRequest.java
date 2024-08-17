package com.studyset.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleEditRequest {
    @NotBlank
    private String title;
    private String isImportant = "false";
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String location;
}
