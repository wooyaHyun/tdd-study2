package com.example.tddstudy2.controller.membership;

import com.example.tddstudy2.dto.membership.MembershipDetailResponseDto;
import com.example.tddstudy2.dto.membership.MembershipRequestDto;
import com.example.tddstudy2.dto.membership.MembershipAddResponseDto;
import com.example.tddstudy2.service.membership.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static com.example.tddstudy2.constants.Constants.USER_ID_HEADER;

@RequiredArgsConstructor
@RestController
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/api/v1/memberships")
    public ResponseEntity<MembershipAddResponseDto> addMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @RequestBody @Valid final MembershipRequestDto requestDto) {

        final MembershipAddResponseDto membershipResponseDto = membershipService.addMembership(userId, requestDto.getMembershipType(), requestDto.getPoint());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(membershipResponseDto);
    }

    @GetMapping("/api/v1/memberships")
    public ResponseEntity<List<MembershipDetailResponseDto>> getMembershipList(
            @RequestHeader(USER_ID_HEADER) final String userId) {

        return ResponseEntity.ok(membershipService.getMembershipList(userId));
    }

    @GetMapping("/api/v1/memberships/{id}")
    public ResponseEntity<MembershipDetailResponseDto> getMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @PathVariable final Long id) {

        return ResponseEntity.ok(membershipService.getMembership(id, userId));
    }

    @DeleteMapping("/api/v1/memberships/{id}")
    public ResponseEntity<Void> removeMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @PathVariable final Long id){

        membershipService.removeMembership(id, userId);
        return ResponseEntity.noContent().build();
    }
}
