package com.studyset.domain;

import com.studyset.domain.enumerate.GroupCategory;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.hibernate.annotations.processing.Pattern;

@Entity
@Table(name = "study_group")
@NoArgsConstructor
@Getter
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
    public Group(String groupName, GroupCategory category, String description, String code) {
        this.groupName = groupName;
        this.category = category;
        this.description = description;
        this.code = code;
    }
}
