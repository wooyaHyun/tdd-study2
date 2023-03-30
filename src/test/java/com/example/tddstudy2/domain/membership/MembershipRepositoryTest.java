package com.example.tddstudy2.domain.membership;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest //JPA Repository들에 대한 빈들을 등록하여 단위 테스트의 작성을 용이하게 함(내부적으로 @ExtentdWith(SpringExtension.class), @Transactional가지고 있음)
public class MembershipRepositoryTest {

    @Autowired
    private MembershipRepository membershipRepository;


    @DisplayName("MembershipRepository가 null이 아님")
    @Test
    void membershipRepositoryIsNotNull(){
        assertThat(membershipRepository).isNotNull();
    }

    @DisplayName("맴버십 등록")
    @Test
    public void membershipInsert(){
        //given
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(1000)
                .build();

        //when
        final Membership result = membershipRepository.save(membership);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(result.getUserId()).isEqualTo("userId");
        assertThat(result.getPoint()).isEqualTo(1000);
    }

    @Test
    void 맴버십이존재하는지테스트(){
        //given
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.KAKAO)
                .point(5000)
                .build();

        //when
        membershipRepository.save(membership);
        final Membership findMembership = membershipRepository.findByUserIdAndMembershipType("userId", MembershipType.KAKAO);

        //then
        assertThat(findMembership).isNotNull();
        assertThat(findMembership.getUserId()).isEqualTo("userId");
        assertThat(findMembership.getMembershipType()).isEqualTo(MembershipType.KAKAO);
    }
}
