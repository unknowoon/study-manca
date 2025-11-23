package com.study.manca.dto;

import com.study.manca.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberRequest {

    private String name;
    private String email;
    private String phone;

    public MemberRequest(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .build();
    }
}
