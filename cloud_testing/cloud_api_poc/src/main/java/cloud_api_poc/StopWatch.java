package cloud_api_poc;

import java.util.concurrent.TimeUnit;

public class StopWatch {

  private long startTime = 0;
  private long endTime = 0;

  public StopWatch() {}

  public StopWatch start() {
    startTime = System.currentTimeMillis();
    return this;
  }

  /** ms to minutes */
  private long minutes(long duration) {
    return TimeUnit.MILLISECONDS.toMinutes(duration);
  }

  /** ms to seconds */
  private long seconds(long duration) {
    return TimeUnit.MILLISECONDS.toSeconds(duration);
  }

  public String end() {
    endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    long minutes = minutes(duration);
    long seconds = seconds(duration) % 60;

    return String.format("%dm %ds", minutes, seconds);
  }

  public StopWatch reset() {
    startTime = 0;
    endTime = 0;
    return this;
  }
}
