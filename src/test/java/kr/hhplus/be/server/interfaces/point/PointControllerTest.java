package kr.hhplus.be.server.interfaces.point;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PointController.class)
class PointControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PointService pointService;

  @DisplayName("포인트 조회 API 테스트")
  @Test
  void findPointTest() throws Exception {
    // given
    long userId = 1L;

    User user = User.builder()
        .id(userId)
        .build();

    UserPoint userPoint = UserPoint.builder()
        .id(1L)
        .user(user)
        .amount(1000L)
        .build();

    when(pointService.findUserPointByUserId(anyLong())).thenReturn(userPoint);

    // when
    // then
    mockMvc.perform(get("/api/v1/points/{userId}", userId))
        .andExpect(status().isOk());
  }

  @DisplayName("포인트 충전 API 테스트")
  @Test
  void chargePointTest() throws Exception {
    // given
    long userId = 1L;
    long amount = 1000L;

    User user = User.builder()
        .id(userId)
        .build();

    UserPoint userPoint = UserPoint.builder()
        .id(1L)
        .user(user)
        .amount(2000L)
        .build();

    when(pointService.charge(anyLong(), anyLong())).thenReturn(userPoint.getAmount());

    // when
    // then
    mockMvc.perform(post("/api/v1/points/{userId}/charge", userId)
            .contentType("application/json")
            .content(String.valueOf(amount)))
        .andExpect(status().isOk());
  }
}