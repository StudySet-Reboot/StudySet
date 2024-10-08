package com.studyset.api.request.schedule;

import com.studyset.domain.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;

@Data
@Slf4j
public class ScheduleCreateForm {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    private Boolean isImportant;

    @NotNull(message = "시작일을 입력해주세요.")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String description;

    private String location;

    public Schedule toEntity(){
        return Schedule.builder()
                .title(title)
                .isImportant(isImportant)
                .description(description)
                .location(location)
                .startTime(startDate)
                .endTime(endDate)
                .isImportant(isImportant)
                .build();
    }
}
