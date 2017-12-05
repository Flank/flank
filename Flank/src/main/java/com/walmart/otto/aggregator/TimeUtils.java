package com.walmart.otto.aggregator;

import java.time.Duration;

class TimeUtils {

  static String formatDuration(Duration duration) {
    long seconds = duration.getSeconds();
    if (seconds < 0) {
      throw new IllegalArgumentException("invalid negative duration");
    }
    return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
  }
}
