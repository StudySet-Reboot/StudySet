package com.studyset.service;

import com.studyset.domain.Group;
import com.studyset.domain.enumerate.GroupCategory;
import com.studyset.repository.GroupRepository;
import com.studyset.web.form.GroupCreateForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupService groupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateGroup() {
        // Given
        GroupCreateForm form = new GroupCreateForm();
        form.setGroupName("Test Group");
        form.setCategory(GroupCategory.PROGRAMMING);
        form.setDescription("A group for science enthusiasts");

        // When
        groupService.createGroup(form);

        // Then
        ArgumentCaptor<Group> groupCaptor = ArgumentCaptor.forClass(Group.class);
        verify(groupRepository, times(1)).save(groupCaptor.capture());
        Group savedGroup = groupCaptor.getValue();

        assertEquals("Test Group", savedGroup.getGroupName());
        assertEquals(GroupCategory.PROGRAMMING, savedGroup.getCategory());
        assertEquals("A group for science enthusiasts", savedGroup.getDescription());
    }
}
