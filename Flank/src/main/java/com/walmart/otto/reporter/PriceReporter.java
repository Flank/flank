package com.walmart.otto.reporter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceReporter {
  private static double virtualDevicePricePerMin = 1.00;
  private static double realDevicePricePerMin = 5.00;

  private static HashMap<String, Double> pricePerMinForDevice = new HashMap<String, Double>();
  private static HashMap<String, BigDecimal> estimates = new HashMap<String, BigDecimal>();

  public static HashMap<String, BigDecimal> getTotalPrice(List<Integer> times) {

    HashMap<String, Double> mapOfPrices = getPricePerMinForDevice();

    for (Map.Entry<String, Double> entry : mapOfPrices.entrySet()) {
      BigDecimal price = new BigDecimal(0.00);
      for (Integer executionTime : times) {
        try {
          price = price.add(calculatePrice(executionTime, entry.getValue()));
        } catch (ParseException e) {
          e.printStackTrace();
        }
        estimates.put(entry.getKey(), price.setScale(2, RoundingMode.HALF_UP)); // round up here
      }
    }
    return estimates;
  }

  public static Double getTotalBillableTime(List<Integer> times) {

    Double time = 0.00;

    for (Integer executionTime : times) {
      time = time + getBillableTime(executionTime);
    }
    return time;
  }

  public static BigDecimal calculatePrice(Number totalTimeInSecs, double pricePerHour)
      throws ParseException {

    Integer billableTime = getBillableTime(totalTimeInSecs);
    BigDecimal pricePerMin =
        new BigDecimal(pricePerHour).divide(new BigDecimal(60), 10, RoundingMode.HALF_UP);
    BigDecimal actualPrice = pricePerMin.multiply(new BigDecimal(billableTime));

    return actualPrice; // this price is not rounded-up because we want to add all the prices and then round-up.
  }

  public static Integer getBillableTime(Number totalTimeInSecs) {
    if (totalTimeInSecs != null) {
      double time = totalTimeInSecs.doubleValue() / 60;
      int roundedTime = (int) Math.ceil(time);
      return roundedTime;
    } else {
      return 0;
    }
  }

  private static HashMap getPricePerMinForDevice() {
    pricePerMinForDevice.put("virtual", virtualDevicePricePerMin);
    pricePerMinForDevice.put("physical", realDevicePricePerMin);
    return pricePerMinForDevice;
  }
}
