package com.studyset.util;

import com.studyset.dto.user.UserDto;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {
    GOOGLE("google", (attributes) -> {
        UserDto userDto = new UserDto();
        userDto.setName((String) attributes.get("name"));
        userDto.setEmail((String) attributes.get("email"));
        return userDto;
    });

    /*NAVER("naver", (attributes) -> {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        System.out.println(response);
        MemberProfile memberProfile = new MemberProfile();
        memberProfile.setName((String) response.get("name"));
        memberProfile.setEmail(((String) response.get("email")));
        return memberProfile;
    }),*/


    private final String registrationId;
    private final Function<Map<String, Object>, UserDto> of;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, UserDto> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static UserDto extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of.apply(attributes);
    }
}
