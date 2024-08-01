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

    private LocalDate parseBirth(String birth) {
        if (birth == null || birth.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            System.err.println("Invalid birth date format: " + birth);
            return null;
        }
    }
}
