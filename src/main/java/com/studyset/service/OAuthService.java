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
    OAuth2 로그인 시 사용자 정보를 처리 및 저장
 */
@Service
@RequiredArgsConstructor
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(OAuthService.class);
    private final UserRepository userRepository;

    /**
     * 유저 정보를 가져옵니다.
     * @param userRequest OAuth2 인증 요청 정보
     * @return OAuth2 로그인 시 생성된 유저 객체
     * @throws OAuth2AuthenticationException OAuth2 인증 예외
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
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

    /**
     * 필요한 유저 속성을 커스텀 합니다.
     * @param attributes OAuth2의 유저 정보
     * @param userNameAttributeName OAuth 로그인 시 유저 이름
     * @param userDto OAuth 서비스에 따른 매핑된 사용자 정보 객체
     * @param registrationId OAuth 서비스 이름
     * @return 커스터마이징 유저 속성 맵
     */
    private Map customAttribute(Map attributes, String userNameAttributeName, UserDto userDto, String registrationId) {
        Map<String, Object> customAttribute = new LinkedHashMap<>();
        customAttribute.put(userNameAttributeName, attributes.get(userNameAttributeName));
        customAttribute.put("provider", registrationId);
        customAttribute.put("name", userDto.getName());
        customAttribute.put("email", userDto.getEmail());
        return customAttribute;
    }

    /**
     * 유저를 저장하거나 업데이트 합니다.
     * @param memberProfile 신규 유저 혹은 기존 유저 DTO
     * @return 저장된 유저
     */
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

