package com.example.tddstudy2.dto.membership;


import com.example.tddstudy2.domain.membership.MembershipType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class MembershipAddResponseDto {

    private final Long id;
    private final MembershipType membershipType;
}
