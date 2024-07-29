package com.studyset.web.form;

import com.studyset.domain.Group;
import com.studyset.domain.enumerate.GroupCategory;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GroupCreateForm {
    @NotEmpty(message = "그룹명은 비어둘 수 없습니다")
    private String groupName;
    private GroupCategory category;
    private String description;

    public Group toEntity(){
        return Group.builder()
                .groupName(groupName)
                .category(category)
                .description(description)
                .build();
    }
}
