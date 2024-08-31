package com.studyset.api.request.schedule;

import lombok.*;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TimeAdjustRequest {

    private List<TimeSlotData> list;
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class TimeSlotData{
        private int day;
        private int time;
    }

}
