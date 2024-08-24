package com.studyset.api.request.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleEditRequest {
    @NotBlank
    private String title;
    private Boolean isImportant;
    @NotNull
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String location;
}
