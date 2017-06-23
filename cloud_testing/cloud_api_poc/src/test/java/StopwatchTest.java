import static cloud_api_poc.Utils.sleep;

import cloud_api_poc.StopWatch;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;

public class StopwatchTest {

  static BigDecimal one = new BigDecimal(1);

  @Test
  public void testStopwatch() {
    StopWatch stopWatch = new StopWatch().start();
    Assert.assertEquals(stopWatch.end(), "0m 0s");

    stopWatch.reset().start();
    sleep(1000);
    Assert.assertEquals(stopWatch.end(), "0m 1s");
  }
}
