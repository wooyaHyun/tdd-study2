package com.example.tddstudy2.service;

import com.example.tddstudy2.domain.membership.Membership;
import com.example.tddstudy2.domain.membership.MembershipRepository;
import com.example.tddstudy2.domain.membership.MembershipType;
import com.example.tddstudy2.dto.membership.MembershipResponseDto;
import com.example.tddstudy2.exception.membership.MembershipErrorResult;
import com.example.tddstudy2.exception.membership.MembershipException;
import com.example.tddstudy2.service.membership.MembershipService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Member;

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
        final MembershipResponseDto result = target.addMembership(userId, membershipType, point);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isEqualTo(-1L);

        //verify
        verify(membershipRepository, times(1)).findByUserIdAndMembershipType(userId, membershipType);
        verify(membershipRepository, times(1)).save(any(Membership.class));
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
