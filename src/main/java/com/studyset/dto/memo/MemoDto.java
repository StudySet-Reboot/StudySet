package com.studyset.dto.memo;

import lombok.Builder;
import lombok.Data;

@Data
public class MemoDto {

    private Long groupId;
    private Long userId;
    private String userName;
    private String contents;

    @Builder
    public MemoDto(Long groupId, Long userId, String userName, String contents) {
        this.groupId = groupId;
        this.userId = userId;
        this.userName = userName;
        this.contents = contents;
    }
}
