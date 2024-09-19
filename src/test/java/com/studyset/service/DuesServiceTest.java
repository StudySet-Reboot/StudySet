package com.studyset.service;

import com.studyset.exception.GroupNotExist;
import com.studyset.exception.UserNotExist;
import com.studyset.domain.Dues;
import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.dto.dues.DuesDto;
import com.studyset.repository.DuesRepository;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.UserRepository;
import com.studyset.web.form.DuesForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DuesServiceTest {

    @Mock
    private DuesRepository duesRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DuesService duesService;

    private DuesDto duesDto;
    private User user;
    private Group group;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .email("test@test.com")
                .name("test")
                .build();

        group = Group.builder()
                .groupName("test group")
                .build();
    }

    @Test
    void getGroupDuesList_ShouldReturnDuesList() {
        // Given
        Long groupId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        DuesDto dto = new DuesDto(user.getName(), 100000.0, LocalDate.now());
        Page<DuesDto> duesDtoPage = new PageImpl<>(Collections.singletonList(dto));
        when(duesRepository.findAllByGroupId(groupId, pageable)).thenReturn(duesDtoPage);

        // When
        Page<DuesDto> result = duesService.getGroupDuesList(groupId, pageable);

        // Then
        assertEquals(duesDtoPage, result);
        assertEquals(1, result.getTotalElements());
        verify(duesRepository, times(1)).findAllByGroupId(groupId, pageable);
    }

    @Test
    void addDues_ShouldSaveDues_WhenGroupAndUserExist() {
        // Given
        Long groupId = 1L;
        DuesForm duesForm = new DuesForm();
        duesForm.setUserId(2L);
        duesForm.setDuesDate(LocalDate.now());
        duesForm.setPrice(1000.0);

        when(groupRepository.findGroupById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findById(duesForm.getUserId())).thenReturn(Optional.of(user));

        // When
        duesService.addDues(groupId, duesForm);

        // Then
        verify(duesRepository, times(1)).save(any(Dues.class));
    }

    @Test
    void addDues_Fail_GroupNotExist() {
        // Given
        Long groupId = 1L;
        DuesForm duesForm = new DuesForm();

        when(groupRepository.findGroupById(groupId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(GroupNotExist.class, () -> duesService.addDues(groupId, duesForm));
        verify(duesRepository, never()).save(any(Dues.class));
    }

    @Test
    void addDues_ShouldThrowException_WhenUserNotExist() {
        // Given
        Long groupId = 1L;
        DuesForm duesForm = new DuesForm();
        duesForm.setUserId(2L);

        when(groupRepository.findGroupById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findById(duesForm.getUserId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotExist.class, () -> duesService.addDues(groupId, duesForm));
        verify(duesRepository, never()).save(any(Dues.class));
    }
}