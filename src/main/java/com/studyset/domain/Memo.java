package com.studyset.domain;

import com.studyset.dto.memo.MemoDto;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter
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

    public MemoDto toDto() {
        return MemoDto.builder()
                .groupId(group.getId())
                .userId(user.getId())
                .contents(contents)
                .build();
    }
}
