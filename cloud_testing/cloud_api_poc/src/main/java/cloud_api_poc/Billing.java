package cloud_api_poc;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Billing {
  private Billing() {}

  static BigDecimal divBy60(long value) {
    return new BigDecimal(value).divide(new BigDecimal(60), 10, RoundingMode.HALF_UP);
  }

  static BigDecimal divBy60(BigDecimal value) {
    return value.divide(new BigDecimal(60), 10, RoundingMode.HALF_UP);
  }

  static BigDecimal PHYSICAL_COST_PER_MIN = divBy60(5); // $5/hr
  static BigDecimal VIRTUAL_COST_PER_MIN = divBy60(1); // $1/hr

  public static long billableMinutes(long testDurationSeconds) {
    return billableMinutes(new BigDecimal(testDurationSeconds));
  }

  public static long billableMinutes(BigDecimal testDurationSeconds) {
    BigDecimal billableMinutes = divBy60(checkForZero(testDurationSeconds));
    // round decimals up.  0.01 minutes is billable at 1 minute.
    return billableMinutes.setScale(0, RoundingMode.UP).longValueExact();
  }

  private static BigDecimal checkForZero(BigDecimal testDurationSeconds) {
    // 0s duration => 1s
    if (testDurationSeconds.compareTo(new BigDecimal(0)) == 0) {
      testDurationSeconds = new BigDecimal(1);
    }

    return testDurationSeconds;
  }

  public static void estimateCosts(long billableMinutes) {
    estimateCosts(new BigDecimal(billableMinutes));
  }

  public static void estimateCosts(BigDecimal billableMinutes) {
    BigDecimal physicalCost =
        billableMinutes.multiply(PHYSICAL_COST_PER_MIN).setScale(2, RoundingMode.HALF_UP);
    BigDecimal virtualCost =
        billableMinutes.multiply(VIRTUAL_COST_PER_MIN).setScale(2, RoundingMode.HALF_UP);

    System.out.println("Billable minutes: " + billableMinutes);
    System.out.println("Physical device cost: $" + physicalCost);
    System.out.println("Virtual  device cost: $" + virtualCost);
  }
}
