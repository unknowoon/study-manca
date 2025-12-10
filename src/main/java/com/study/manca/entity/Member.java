package com.study.manca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String phone;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberGrade grade = MemberGrade.BRONZE;

    public enum MemberGrade {
        BRONZE,     // 기본
        SILVER,     // 누적 이용 10회 이상
        GOLD,       // 누적 이용 30회 이상
        VIP         // 누적 이용 100회 이상
    }
}
