package com.studyset.controller;

import com.studyset.service.TaskSubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class TaskSubmissionController {
    private final TaskSubmissionService taskSubmissionService;

    // 과제 수정 페이지 이동

    // 과제 수정

    // 과제 삭제

}
