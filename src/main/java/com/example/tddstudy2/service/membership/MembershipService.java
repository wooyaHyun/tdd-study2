package com.example.tddstudy2.service.membership;

import com.example.tddstudy2.domain.membership.Membership;
import com.example.tddstudy2.domain.membership.MembershipRepository;
import com.example.tddstudy2.domain.membership.MembershipType;
import com.example.tddstudy2.dto.membership.MembershipResponseDto;
import com.example.tddstudy2.exception.membership.MembershipErrorResult;
import com.example.tddstudy2.exception.membership.MembershipException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipResponseDto addMembership(final String userId, final MembershipType membershipType, final Integer point) {
        final Membership result = membershipRepository.findByUserIdAndMembershipType(userId, membershipType);
        if (result != null) {
            throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
        }

        final Membership savedMembership = membershipRepository.save(Membership.builder().userId(userId).membershipType(membershipType).point(point).build());

        return MembershipResponseDto.builder()
                .id(savedMembership.getId())
                .membershipType(savedMembership.getMembershipType())
                .build();
    }
}
