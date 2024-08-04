package com.studyset.web.form;

import com.studyset.domain.Group;
import com.studyset.domain.enumerate.GroupCategory;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GroupCreateForm {
    @NotEmpty(message = "그룹명을 입력해주세요")
    private String groupName;
    private GroupCategory category;
    private String description;
    @NotEmpty(message = "코드를 입력해주세요")
    private String code;

    public Group toEntity(){
        return Group.builder()
                .groupName(groupName)
                .category(category)
                .description(description)
                .code(code)
                .build();
    }
}
