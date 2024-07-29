package com.studyset.dto.group;

import com.studyset.domain.Group;
import com.studyset.domain.enumerate.GroupCategory;
import lombok.Builder;
import lombok.Data;

@Data
public class GroupDto {
    private String groupName;
    private GroupCategory category;
    private String description;

    @Builder
    public GroupDto(String groupName, GroupCategory category, String description) {
        this.groupName = groupName;
        this.category = category;
        this.description = description;
    }
}
