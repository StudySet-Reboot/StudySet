package com.studyset.web.form;

import com.studyset.domain.Schedule;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;

@Data
public class ScheduleCreateForm {
    @NotBlank
    private String title;
    private String isImportant = "false";
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String location;

    public Schedule toEntity(){
        boolean isImportant = getIsImportant().equals("true") ? true : false;
        return Schedule.builder()
                .title(title)
                .isImportant(isImportant)
                .description(description)
                .location(location)
                .startTime(startDate)
                .endTime(endDate)
                .build();
    }
}
