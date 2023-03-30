package com.example.tddstudy2.dto.membership;

import com.example.tddstudy2.domain.membership.MembershipType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Builder
public class MembershipRequestDto {

    @NotNull
    @Min(0)
    private final int point;

    @NotNull
    private final MembershipType membershipType;
}
