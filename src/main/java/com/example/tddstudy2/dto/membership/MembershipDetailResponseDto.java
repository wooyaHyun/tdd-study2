package com.example.tddstudy2.dto.membership;

import com.example.tddstudy2.domain.membership.MembershipType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * packageName : com.example.tddstudy2.dto.membership
 * fileName : MemebershipDetailResponse
 * author : SHW
 * date : 2023-04-01
 * description :
 * ===========================================================
 * DATE      AUTHOR      NOTE
 * -----------------------------------------------------------
 * 2023-04-01   SHW     최초 생성
 */

@Getter
@Builder
@RequiredArgsConstructor
public class MembershipDetailResponseDto {

    private final Long id;
    private final String userId;
    private final MembershipType membershipType;
    private final int point;
    private final LocalDateTime createdDate;
}
