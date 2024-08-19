package com.studyset.interceptor;

import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.service.GroupService;
import com.studyset.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupAccessInterceptor implements HandlerInterceptor {
    private final GroupService groupService;
    private final UserService userService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // URL에서 groupId 추출
        Long groupId = extractGroupIdFromUri(request.getRequestURI());

        if (groupId != null) {
            // groupId가 있는 경우 처리
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access.");
                return false;
            }

            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
            String provider = authToken.getAuthorizedClientRegistrationId();
            OAuth2User oAuth2User = (OAuth2User) authToken.getPrincipal();
            String email = (String) oAuth2User.getAttribute("email");

            Optional<User> userOptional = userService.findByEmailAndProvider(email, provider);
            if (userOptional.isEmpty()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not found.");
                return false;
            }
            User user = userOptional.get();

            // 그룹 조회 및 사용자 그룹 멤버 여부 확인
            Group group = groupService.findGroupById(groupId);
            if (!groupService.isUserMemberOfGroup(user, group)) {
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().write("<script>alert('접근 불가: 그룹에 가입해주세요.'); window.location.href='/users/main';</script>");
                //response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: You are not a member of the group.");
                return false;
            }

            return true;
        } else {
            // groupId가 없는 url에 대해서는 통과
            return true;
        }
    }

    private Long extractGroupIdFromUri(String uri) {
        // URI에서 groupId 추출
        Pattern pattern = Pattern.compile("/groups/(\\d+).*"); // 숫자로 된 groupId를 추출
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1)); // groupId를 Long 타입으로 변환
        }
        return null;
    }
}
