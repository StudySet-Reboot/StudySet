package com.studyset.service;

import com.studyset.repository.DuesRepository;
import com.studyset.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DuesService {
    private final DuesRepository duesRepository;

}
