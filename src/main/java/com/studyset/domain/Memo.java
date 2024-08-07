package com.studyset.domain;

import com.studyset.dto.memo.MemoDto;
import jakarta.persistence.*;
import lombok.Builder;

@Entity
public class Memo extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false)
    private String contents;

    @Builder
    public Memo(Long id, Group group, User user, String contents) {
        this.id = id;
        this.group = group;
        this.user = user;
        this.contents = contents;
    }

    public MemoDto toDto() {
        return MemoDto.builder()
                .id(id)
                .group(group)
                .user(user)
                .contents(contents)
                .build();
    }
}
