package com.studyset.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class OAuthController {

    // 최초 접속 화면
    @GetMapping("/login")
    public String login() {
        return "thyme/login";
    }
}
