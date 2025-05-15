package kr.hhplus.be.server.support.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class  TimeUtil {

  public static Between getBetweenDayRangeFromNow(int day) {
    LocalDate now = LocalDate.now();
    LocalDateTime from = now.minusDays(day).atStartOfDay();
    LocalDateTime to = LocalDateTime.now();

    return new Between(from, to);
  }

  public static Between getBetweenHourRangeFromNow(int hour) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime from = now.minusHours(hour);

    return new Between(from.truncatedTo(ChronoUnit.HOURS), now.truncatedTo(ChronoUnit.HOURS));
  }

  public record Between(
      LocalDateTime from,
      LocalDateTime to
  ) {

  }
}
