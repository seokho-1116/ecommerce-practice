package kr.hhplus.be.server.support.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import kr.hhplus.be.server.support.util.TimeUtil.Between;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TimeUtilTest {

  @DisplayName("현재와 n일 전의 날짜를 구한다")
  @Test
  void getBetweenNow() {
    // given
    int day = 3;

    // when
    Between between = TimeUtil.getBetweenDayRangeFromNow(day);

    // then
    assertThat(between.from()).isEqualTo(LocalDate.now().minusDays(day).atStartOfDay());
    assertThat(between.to().truncatedTo(ChronoUnit.SECONDS))
        .isEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
  }

  @DisplayName("현재에서 n시간 전의 날짜를 구한다")
  @Test
  void getBetweenHourNow() {
    // given
    int hour = 2;

    // when
    Between between = TimeUtil.getBetweenHourRangeFromNow(hour);

    // then
    assertThat(between.from())
        .isEqualTo(LocalDateTime.now().minusHours(hour).truncatedTo(ChronoUnit.HOURS));
    assertThat(between.to())
        .isEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));
  }
}