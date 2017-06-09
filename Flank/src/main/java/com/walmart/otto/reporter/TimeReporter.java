package com.walmart.otto.reporter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeReporter {
  private static List<Integer> executionTimes = new ArrayList<>();

  public static void addExecutionTime(int time) {
    executionTimes.add(time);
  }

  public static String getEndTime() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_hh:mm");
    Date date = new Date();
    return simpleDateFormat.format(date);
  }

  public static String getTotalTime(long startTime) {
    long totalTime = System.currentTimeMillis() - startTime;

    return String.format(
        "%d min, %d sec",
        TimeUnit.MILLISECONDS.toMinutes(totalTime),
        TimeUnit.MILLISECONDS.toSeconds(totalTime)
            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTime)));
  }

  public static List<Integer> getExecutionTimes() {
    return executionTimes;
  }
}
