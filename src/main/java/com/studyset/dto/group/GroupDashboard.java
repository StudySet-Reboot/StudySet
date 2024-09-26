package com.studyset.dto.group;

import com.studyset.dto.dues.DuesInfo;
import com.studyset.dto.memo.MemoDto;
import com.studyset.dto.task.TaskDto;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
public class GroupDashboard {

    private final Long groupId;
    private final DuesInfo duesInfo;
    private final List<TaskDto> taskList;
    private final List<MemoDto> memoList;

    @Builder
    public GroupDashboard(Long groupId, DuesInfo duesInfo, List<TaskDto> taskList, List<MemoDto> memoList) {
        this.groupId = groupId;
        this.duesInfo = duesInfo;
        this.taskList = taskList;
        this.memoList = memoList;
    }
}
