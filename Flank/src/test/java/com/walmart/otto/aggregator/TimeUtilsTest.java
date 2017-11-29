package com.walmart.otto.aggregator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.Duration;
import org.junit.Test;

public class TimeUtilsTest {

  @Test
  public void shouldFormatDurationInTheFullFormat() throws Exception {
    String duration = TimeUtils.formatDuration(Duration.ofSeconds(3600 + 60 + 5));
    assertThat(duration, equalTo("01:01:05"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowOnNegativeDuration() throws Exception {
    TimeUtils.formatDuration(Duration.ofSeconds(-10));
  }
}
