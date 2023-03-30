package com.example.tddstudy2.domain.membership;

import com.example.tddstudy2.domain.BaseEntity;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class Membership extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String userId;

    @Enumerated(EnumType.STRING)
    private MembershipType membershipType;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int point;

/*    @Builder
    public Membership(String userId, MembershipType membershipType, int point){
        this.userId = userId;
        this.membershipType = membershipType;
        this.point = point;
    }*/



}
