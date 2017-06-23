import static cloud_api_poc.Billing.billableMinutes;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BillingTest {

  @Test
  public void testBillableMinutes() {
    assertEquals(1, billableMinutes(0));

    assertEquals(1, billableMinutes(3));
    assertEquals(2, billableMinutes(61));
  }
}
