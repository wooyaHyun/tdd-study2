package com.example.tddstudy2.domain.membership;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MembershipType {
    NAVER("naver"),
    KAKAO("kakao"),
    LINE("line")
    ;

    private final String companyName;
}
