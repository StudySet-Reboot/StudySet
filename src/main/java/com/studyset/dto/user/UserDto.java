package com.studyset.dto.user;

import com.studyset.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Getter @Setter
public class UserDto {
    private String name;
    private String provider;
    private String phone;
    private String email;
    private String birth;

    public User toMember() {
        return User.builder()
                .name(name)
                .provider(provider)
                .phone(phone)
                .email(email)
                .build();
    }

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.name = user.getName();
        userDto.provider = user.getProvider();
        userDto.phone = user.getPhone();
        userDto.email = user.getEmail();
        return userDto;
    }
}
