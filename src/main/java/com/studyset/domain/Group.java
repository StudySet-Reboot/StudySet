package com.studyset.domain;

import com.studyset.domain.enumerate.GroupCategory;
import com.studyset.dto.group.GroupDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.processing.Pattern;

@Entity
@Table(name = "study_group", uniqueConstraints = @UniqueConstraint(name = "name_code_unique", columnNames = {"groupName", "code"}))
@NoArgsConstructor
@Setter @Getter
public class Group extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;
    private String groupName;
    @Enumerated(EnumType.STRING)
    private GroupCategory category;
    private String description;
    @Column(columnDefinition = "char(6)")
    private String code;
    @Builder
    public Group(Long id, String groupName, GroupCategory category, String description, String code) {
        this.id = id;
        this.groupName = groupName;
        this.category = category;
        this.description = description;
        this.code = code;
    }
    public GroupDto toDto(){
        return GroupDto.builder()
                .id(id)
                .groupName(groupName)
                .category(category)
                .description(description)
                .code(code)
                .build();
    }
}
