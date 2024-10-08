package com.studyset.interceptor;

import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.exception.ForbiddenAccess;
import com.studyset.exception.UserNotExist;
import com.studyset.exception.response.ErrorCode;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupAccessInterceptor implements HandlerInterceptor {

    private final GroupService groupService;
    private final UserService userService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Long groupId = extractGroupIdFromUri(request.getRequestURI());

        if (groupId != null) {
            User user = getAuthenticatedUser();
            if (user == null) {
                throw new UserNotExist(ErrorCode.UNAUTHORIZED_USER);
            }

            Group group = groupService.findGroupById(groupId);
            if (!groupService.isUserMemberOfGroup(user, group)) {
                throw new ForbiddenAccess(ErrorCode.FORBIDDEN_USER);
            }
        }

        return true;
    }

    private Long extractGroupIdFromUri(String uri) {
        Pattern pattern = Pattern.compile("/groups/(\\d+).*");
        Matcher matcher = pattern.matcher(uri);
        return matcher.find() ? Long.parseLong(matcher.group(1)) : null;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            return null;
        }

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String provider = authToken.getAuthorizedClientRegistrationId();
        OAuth2User oAuth2User = authToken.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        return userService.findByEmailAndProvider(email, provider).orElse(null);
    }

}

