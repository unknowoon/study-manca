package com.study.manca.dto;

import com.study.manca.entity.Member;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String phone;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.phone = member.getPhone();
        this.createdAt = member.getCreatedAt();
        this.updatedAt = member.getUpdatedAt();
    }

    public static MemberResponse from(Member member) {
        return new MemberResponse(member);
    }
}
