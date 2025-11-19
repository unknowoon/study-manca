package com.study.manca.dto;

import com.study.manca.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequest {

    private String name;
    private String email;
    private String phone;

    public UserRequest(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .build();
    }
}
