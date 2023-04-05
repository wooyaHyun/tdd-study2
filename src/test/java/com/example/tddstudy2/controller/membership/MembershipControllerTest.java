package com.example.tddstudy2.controller.membership;

import com.example.tddstudy2.domain.membership.MembershipType;
import com.example.tddstudy2.dto.membership.MembershipDetailResponseDto;
import com.example.tddstudy2.dto.membership.MembershipRequestDto;
import com.example.tddstudy2.dto.membership.MembershipAddResponseDto;
import com.example.tddstudy2.exception.GlobalExceptionHandler;
import com.example.tddstudy2.exception.membership.MembershipErrorResult;
import com.example.tddstudy2.exception.membership.MembershipException;
import com.example.tddstudy2.service.membership.MembershipService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static com.example.tddstudy2.constants.Constants.USER_ID_HEADER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MembershipControllerTest {

    //@LocalServerPort
    //private int port;

    @InjectMocks
    private MembershipController target;

    @Mock
    private MembershipService membershipService;

    private MockMvc mockMvc;
    private Gson gson;

    @BeforeEach
    void init(){
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

    }

    /*@Test
    void mockMvc가null이아님() {

        assertThat(target).isNotNull();
        assertThat(mockMvc).isNotNull();
    }*/

    @Test
    void 맴버십등록실패_사용자식별값이헤더에없음() throws Exception {
        //given
        final String url = "/api/v1/memberships";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(membershipRequestDto(1000, MembershipType.KAKAO)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());

    }

    /*
    @Test
    void 맴버십등록실패_포인트가음수() throws Exception {
        //given
        final String url = "/api/v1/memberships";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "TEST-ID")
                        .content(gson.toJson(membershipRequestDto(-5000000, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 맴버십등록실패_맴버십타입이null() throws Exception {
        //given
        final String url = "/api/v1/memberships";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "TEST-ID")
                        .content(gson.toJson(membershipRequestDto(1513145, null)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }
    */

    @Test
    void 맴버십등록실패_MembershipService에서에러throw() throws Exception {
        //given
        final String url = "/api/v1/memberships";

        when(membershipService.addMembership("12345", MembershipType.NAVER, 10000)).thenThrow(new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER));

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(gson.toJson(membershipRequestDto(10000, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );


        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 멤버십등록성공() throws Exception {
        //given
        final String url = "/api/v1/memberships";

        MembershipAddResponseDto responseDto = MembershipAddResponseDto.builder()
                .id(-1L)
                .membershipType(MembershipType.KAKAO)
                .build();

        when(membershipService.addMembership("12345", MembershipType.KAKAO, 5555))
                .thenReturn(responseDto);

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(membershipRequestDto(5555, MembershipType.KAKAO)))
        );

        //then
        resultActions.andExpect(status().isCreated());

        final MembershipAddResponseDto result = gson.fromJson(resultActions.andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), MembershipAddResponseDto.class);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getMembershipType()).isEqualTo(responseDto.getMembershipType());
    }


    @ParameterizedTest
    @MethodSource("invalidMembershipAddParameter")
    void 맴버십등록실패_잘못된파라미터(final int point, final MembershipType membershipType) throws Exception {
        //given
        final String url = "/api/v1/memberships";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(gson.toJson(membershipRequestDto(point, membershipType)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> invalidMembershipAddParameter() {

        return Stream.of(
                Arguments.of(-5000000, MembershipType.NAVER),
                Arguments.of(1513145, null)
        );
    }

    @Test
    void 멤버십목록조회실패_사용자식별값이헤더에없음() throws Exception {
        //given
        final String url = "/api/v1/memberships";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 멤버십목록조회성공() throws Exception {
        //given
        final String url = "/api/v1/memberships";
        when(membershipService.getMembershipList("12345"))
                .thenReturn(
                        List.of(
                                MembershipDetailResponseDto.builder().build(),
                                MembershipDetailResponseDto.builder().build(),
                                MembershipDetailResponseDto.builder().build()
                        )
                );

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(USER_ID_HEADER, "12345")
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void 멤버십상세조회실패_사용자식벽헤더값없음() throws Exception {
        //give
        final String url = "/api/v1/memberships";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 멤버십상세조회_멤버십이존재하지않을때() throws Exception {
        final String url = "/api/v1/memberships/-1";

        when(membershipService.getMembership(-1L, "12345")).thenThrow(new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(USER_ID_HEADER, "12345")
        );

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void 멤버십상세조회성공() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1";
        when(membershipService.getMembership(-1L, "12345")).thenReturn(MembershipDetailResponseDto.builder().build());

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(USER_ID_HEADER, "12345")
                        .param("membershipType", MembershipType.NAVER.name())
        );

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 멤버십삭제실패_사용자식별값이헤더에없음() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십삭제성공() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
                        .header(USER_ID_HEADER, "12345")
        );

        // then
        resultActions.andExpect(status().isNoContent());
    }

    private MembershipRequestDto membershipRequestDto(final int point, final MembershipType membershipType) {
        return MembershipRequestDto.builder()
                .point(point)
                .membershipType(membershipType)
                .build();
    }
}
