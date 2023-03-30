package com.example.tddstudy2.controller.membership;

import com.example.tddstudy2.domain.membership.MembershipType;
import com.example.tddstudy2.dto.membership.MembershipRequestDto;
import com.example.tddstudy2.exception.GlobalExceptionHandler;
import com.example.tddstudy2.exception.membership.MembershipErrorResult;
import com.example.tddstudy2.exception.membership.MembershipException;
import com.example.tddstudy2.service.membership.MembershipService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.example.tddstudy2.constants.Constants.USER_ID_HEADER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MembershipControllerTest {

    @LocalServerPort
    private int port;

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
        final String url = "http://localhost:"+port+"/api/v1/memberships";


        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(membershipRequestDto(1000, MembershipType.KAKAO)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());

    }

    @Test
    void 맴버십등록실패_포인트가음수() throws Exception {
        //given
        final String url = "http://localhost:"+port+"/api/v1/memberships";

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
        final String url = "http://localhost:"+port+"/api/v1/memberships";

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

    @Test
    void 맴버십등록실패_MembershipService에서에러throw() throws Exception {
        //given
        final String url = "http://localhost:"+port+"/api/v1/memberships";

        //when(membershipService.addMembership("12345", MembershipType.NAVER, 10000)).thenThrow(new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER));
        doThrow(new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER))
                .when(membershipService)
                .addMembership("12345", MembershipType.NAVER, 10000);

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

    private MembershipRequestDto membershipRequestDto(final int point, final MembershipType membershipType) {
        return MembershipRequestDto.builder()
                .point(point)
                .membershipType(membershipType)
                .build();
    }
}
