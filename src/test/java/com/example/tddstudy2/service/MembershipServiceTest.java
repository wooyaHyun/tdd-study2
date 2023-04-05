package com.example.tddstudy2.service;

import com.example.tddstudy2.domain.membership.Membership;
import com.example.tddstudy2.domain.membership.MembershipRepository;
import com.example.tddstudy2.domain.membership.MembershipType;
import com.example.tddstudy2.dto.membership.MembershipAddResponseDto;
import com.example.tddstudy2.dto.membership.MembershipDetailResponseDto;
import com.example.tddstudy2.exception.membership.MembershipErrorResult;
import com.example.tddstudy2.exception.membership.MembershipException;
import com.example.tddstudy2.service.membership.MembershipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {

    @InjectMocks
    private MembershipService target;

    @Mock
    private MembershipRepository membershipRepository;

    private final Long membershipId = -1L;
    private final String userId = "userId";
    private final MembershipType membershipType = MembershipType.KAKAO;
    private final int point = 10000;

    @Test
    void 맴버십등록실패_이미존재함() {
        //given
        when(membershipRepository.findByUserIdAndMembershipType(userId, membershipType)).thenReturn(Membership.builder().build());
        //doReturn(Membership.builder().build()).when(membershipRepository).findByUserIdAndMembershipType(userId, membershipType);

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.addMembership(userId, membershipType, point));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
    }


    @Test
    void 맴버십등록성공() {
        //given
        when(membershipRepository.findByUserIdAndMembershipType(userId, membershipType)).thenReturn(null);
        when(membershipRepository.save(any(Membership.class))).thenReturn(membership());

        //when
        final MembershipAddResponseDto result = target.addMembership(userId, membershipType, point);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isEqualTo(-1L);

        //verify
        verify(membershipRepository, times(1)).findByUserIdAndMembershipType(userId, membershipType);
        verify(membershipRepository, times(1)).save(any(Membership.class));
    }


    @Test
    void 멤버십목록조회() {
        when(membershipRepository.findAllByUserId(userId)).thenReturn(
                List.of(
                        Membership.builder().build(),
                        Membership.builder().build(),
                        Membership.builder().build()
                )
        );

        final List<MembershipDetailResponseDto> result =target.getMembershipList(userId);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    void 멤버십상세조회실패_존재하지않음() {
        //given
        when(membershipRepository.findById(membershipId)).thenReturn(Optional.empty());

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.getMembership(membershipId, userId));

        // then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십상세조회실패_본인이아님() {
        // given
        when(membershipRepository.findById(membershipId)).thenReturn(Optional.empty());

        // when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.getMembership(membershipId, "notowner"));

        // then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    void 멤버십상세조회성공() {
        //given
        when(membershipRepository.findById(membershipId)).thenReturn(Optional.of(membership()));

        //when
        final MembershipDetailResponseDto result = target.getMembership(membershipId, userId);

        //then
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.KAKAO);
        assertThat(result.getPoint()).isEqualTo(point);
    }


    @Test
    void 멤버십삭제실패_존재하지않음() {
        //given
        when(membershipRepository.findById(membershipId)).thenReturn(Optional.empty());

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.removeMembership(membershipId, userId));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십삭제실패_본인이아님() {
        // given
        final Membership membership = membership();
        when(membershipRepository.findById(membershipId)).thenReturn(Optional.of(membership));

        // when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.removeMembership(membershipId, "notowner"));

        // then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
    }

    @Test
    void 멤버십삭제성공() {
        //given
        final Membership membership = membership();
        when(membershipRepository.findById(membershipId)).thenReturn(Optional.of(membership));

        //when
        target.removeMembership(membershipId, userId);
    }

    private Membership membership() {
        return Membership.builder()
                .id(-1L)
                .userId(userId)
                .point(point)
                .membershipType(MembershipType.KAKAO)
                .build();
    }

}
