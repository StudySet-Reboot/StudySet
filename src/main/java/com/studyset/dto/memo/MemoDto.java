package com.studyset.dto.memo;

import com.studyset.domain.Group;
import com.studyset.domain.User;
import lombok.Builder;
import lombok.Data;

@Data
public class MemoDto {
    private Group group;
    private User user;
    private String contents;

    @Builder
    public MemoDto(Group group, User user, String contents) {
        this.group = group;
        this.user = user;
        this.contents = contents;
    }
}
