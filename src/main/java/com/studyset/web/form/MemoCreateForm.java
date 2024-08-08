package com.studyset.web.form;

import com.studyset.domain.Memo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class MemoCreateForm {
    private Long userId;
    private Long groupId;
    private String content;
}
