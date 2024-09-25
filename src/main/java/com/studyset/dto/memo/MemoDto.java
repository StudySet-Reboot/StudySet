package com.studyset.dto.memo;

import lombok.Builder;
import lombok.Data;

@Data
public class MemoDto {

    private Long groupId;
    private Long userId;
    private String contents;

    @Builder
    public MemoDto(Long groupId, Long userId, String contents) {
        this.groupId = groupId;
        this.userId = userId;
        this.contents = contents;
    }
}
