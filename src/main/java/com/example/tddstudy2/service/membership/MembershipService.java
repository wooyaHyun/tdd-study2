package com.example.tddstudy2.service.membership;

import com.example.tddstudy2.domain.membership.Membership;
import com.example.tddstudy2.domain.membership.MembershipRepository;
import com.example.tddstudy2.domain.membership.MembershipType;
import com.example.tddstudy2.dto.membership.MembershipAddResponseDto;
import com.example.tddstudy2.dto.membership.MembershipDetailResponseDto;
import com.example.tddstudy2.exception.membership.MembershipErrorResult;
import com.example.tddstudy2.exception.membership.MembershipException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipAddResponseDto addMembership(final String userId, final MembershipType membershipType, final Integer point) {
        final Membership result = membershipRepository.findByUserIdAndMembershipType(userId, membershipType);
        if (result != null) {
            throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
        }

        final Membership savedMembership = membershipRepository.save(Membership.builder().userId(userId).membershipType(membershipType).point(point).build());

        return MembershipAddResponseDto.builder()
                .id(savedMembership.getId())
                .membershipType(savedMembership.getMembershipType())
                .build();
    }

    public List<MembershipDetailResponseDto> getMembershipList(final String userId) {
        List<Membership> membershipList = membershipRepository.findAllByUserId(userId);

        return membershipList.stream()
                .map(v -> MembershipDetailResponseDto.builder()
                        .id(v.getId())
                        .membershipType(v.getMembershipType())
                        .point(v.getPoint())
                        .createdDate(v.getCreatedDate())
                        .build()
                ).collect(Collectors.toList());
    }

    public MembershipDetailResponseDto getMembership(final Long membershipId, final String userId){
        //final Optional<Membership> optionalMembership = membershipRepository.findById(membershipId);
        //Membership membership = optionalMembership.orElseThrow(() -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));

        final Membership membership = membershipRepository.findById(membershipId).orElseThrow(() -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));
        if(!membership.getUserId().equals(userId)){
            throw new IllegalArgumentException("NOT_MEMBERSHIP_OWNER");
        }

        return MembershipDetailResponseDto.builder()
                .id(membership.getId())
                .membershipType(membership.getMembershipType())
                .point(membership.getPoint())
                .createdDate(membership.getCreatedDate())
                .build();
    }

    public void removeMembership(final Long membershipId, final String userId) {

        final Optional<Membership> optionalMembership = membershipRepository.findById(membershipId);
        final Membership membership = optionalMembership.orElseThrow(() -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));
        if (!membership.getUserId().equals(userId)) {
            throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
        }

        membershipRepository.deleteById(membershipId);
    }
}
