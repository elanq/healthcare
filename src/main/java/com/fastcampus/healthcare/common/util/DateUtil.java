package com.fastcampus.healthcare.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateUtil {

  public static Date convertLocalDateTimeToDate(LocalDateTime dateTime) {
    ZoneId zoneId = ZoneId.systemDefault();
    ZonedDateTime zonedDateTime = dateTime.atZone(zoneId);
    return Date.from(zonedDateTime.toInstant());
  }

}
