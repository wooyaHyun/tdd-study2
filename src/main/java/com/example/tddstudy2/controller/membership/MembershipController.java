package com.example.tddstudy2.controller.membership;

import com.example.tddstudy2.dto.membership.MembershipRequestDto;
import com.example.tddstudy2.dto.membership.MembershipResponseDto;
import com.example.tddstudy2.service.membership.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.example.tddstudy2.constants.Constants.USER_ID_HEADER;

@RequiredArgsConstructor
@RestController
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/api/v1/memberships")
    public ResponseEntity<MembershipResponseDto> addMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @RequestBody @Valid final MembershipRequestDto requestDto) {

        membershipService.addMembership(userId, requestDto.getMembershipType(), requestDto.getPoint());

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }
}
