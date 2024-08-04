package com.studyset.dto.group;

import com.studyset.domain.Group;
import com.studyset.domain.enumerate.GroupCategory;
import lombok.Builder;
import lombok.Data;

@Data
public class GroupDto {
    private Long id;
    private String groupName;
    private GroupCategory category;
    private String description;
    private String code;

    @Builder
    public GroupDto(Long id, String groupName, GroupCategory category, String description, String code) {
        this.id = id;
        this.groupName = groupName;
        this.category = category;
        this.description = description;
        this.code = code;
    }
}
