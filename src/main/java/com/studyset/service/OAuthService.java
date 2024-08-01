package com.studyset.service;

import com.studyset.domain.User;
import com.studyset.dto.user.UserDto;
import com.studyset.repository.UserRepository;
import com.studyset.util.OAuthAttributes;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/*
    OAuth2 로그인 성공시 DB에 저장
 */
@Service
@RequiredArgsConstructor
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(OAuthService.class);
    private final UserRepository userRepository;
    private static final String APPLICATION_NAME = "StudySet";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        // OAuth에서 가져온 유저 정보
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        // OAuth 서비스 이름
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // OAuth 로그인 시 키(pk)
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        // OAuth 서비스의 유저 정보들
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // registrationId에 따라 유저 정보를 통해 공통된 UserProfile 객체로 만들어 줌
        UserDto userDto = OAuthAttributes.extract(registrationId, attributes);
        userDto.setProvider(registrationId);

        User user = saveOrUpdate(userDto);
        Map<String, Object> customAttribute = customAttribute(attributes, userNameAttributeName, userDto, registrationId);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                customAttribute,
                userNameAttributeName);
    }

    private Map customAttribute(Map attributes, String userNameAttributeName, UserDto memberProfile, String registrationId) {
        Map<String, Object> customAttribute = new LinkedHashMap<>();
        customAttribute.put(userNameAttributeName, attributes.get(userNameAttributeName));
        customAttribute.put("provider", registrationId);
        customAttribute.put("name", memberProfile.getName());
        customAttribute.put("email", memberProfile.getEmail());
        return customAttribute;
    }

    private User saveOrUpdate(UserDto memberProfile) {
        User member = userRepository.findByEmailAndProvider(memberProfile.getEmail(), memberProfile.getProvider())
                .map(m -> {
                    logger.info("기존 유저 updating: " + m);
                    return m.update(memberProfile.getName(), memberProfile.getEmail());
                })
                .orElseGet(() -> {
                    logger.info("신규 유저 creating: " + memberProfile);
                    return memberProfile.toMember();
                });

        User savedUser = userRepository.save(member);
        logger.info("Saved user: " + savedUser);

        // 유저 정보 세션에 저장
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.getSession().setAttribute("user", savedUser);

        logger.info("User saved in session: " + request.getSession().getAttribute("user"));
        return savedUser;
    }

}

