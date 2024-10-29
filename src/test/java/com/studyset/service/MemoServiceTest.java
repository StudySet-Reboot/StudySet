package com.studyset.service;

import com.studyset.domain.Group;
import com.studyset.domain.Memo;
import com.studyset.domain.User;
import com.studyset.dto.memo.MemoDto;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.MemoRepository;
import com.studyset.repository.UserRepository;
import com.studyset.web.form.MemoCreateForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemoServiceTest {
    @InjectMocks
    private MemoService memoService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private MemoRepository memoRepository;

    private User user;
    private Group group;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        group = new Group();
        group.setId(1L);
    }

    @Test
    @DisplayName("메모 작성")
    void writeMemo() {
        // given
        MemoCreateForm memoForm = new MemoCreateForm();
        memoForm.setUserId(1L);
        memoForm.setGroupId(1L);
        memoForm.setContent("memo test");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(memoRepository.save(any(Memo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MemoDto result = memoService.addMemo(memoForm);

        // then
        assertNotNull(result);
        assertEquals("memo test", result.getContents());
        verify(memoRepository, times(1)).save(any(Memo.class));
    }
}
