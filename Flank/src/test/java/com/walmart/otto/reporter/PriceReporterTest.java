package com.walmart.otto.reporter;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;

public class PriceReporterTest {
  @Test
  public void testCalculatePrice() throws ParseException {

    BigDecimal price1 = PriceReporter.calculatePrice(125, 5);
    assertEquals("0.25", price1.setScale(2, RoundingMode.HALF_UP).toString());

    BigDecimal price2 = PriceReporter.calculatePrice(67, 1);
    assertEquals("0.03", price2.setScale(2, RoundingMode.HALF_UP).toString());

    BigDecimal price3 = PriceReporter.calculatePrice(60, 1);
    assertEquals("0.02", price3.setScale(2, RoundingMode.HALF_UP).toString());

    BigDecimal price4 = PriceReporter.calculatePrice(60, 5);
    assertEquals("0.08", price4.setScale(2, RoundingMode.HALF_UP).toString());

    BigDecimal price5 = PriceReporter.calculatePrice(120, 5);
    assertEquals("0.17", price5.setScale(2, RoundingMode.HALF_UP).toString());

    BigDecimal price6 = PriceReporter.calculatePrice(123.56, 5);
    assertEquals("0.25", price6.setScale(2, RoundingMode.HALF_UP).toString());

    BigDecimal price7 = PriceReporter.calculatePrice(123.56, 1);
    assertEquals("0.05", price7.setScale(2, RoundingMode.HALF_UP).toString());

    BigDecimal price8 = PriceReporter.calculatePrice(123.5623344233434343, 1);
    assertEquals("0.05", price8.setScale(2, RoundingMode.HALF_UP).toString());

    BigDecimal price9 = PriceReporter.calculatePrice(0, 1);
    assertEquals("0.00", price9.setScale(2, RoundingMode.HALF_UP).toString());

    BigDecimal price10 = PriceReporter.calculatePrice(0.45, 5);
    assertEquals("0.08", price10.setScale(2, RoundingMode.HALF_UP).toString());

    BigDecimal price11 = PriceReporter.calculatePrice(0.599999, 5);
    assertEquals("0.08", price11.setScale(2, RoundingMode.HALF_UP).toString());

    BigDecimal price12 = PriceReporter.calculatePrice(0.599999, 1);
    assertEquals("0.02", price12.setScale(2, RoundingMode.HALF_UP).toString());

    BigDecimal price13 = PriceReporter.calculatePrice(-0.599999, 1);
    assertEquals("0.00", price13.setScale(2, RoundingMode.HALF_UP).toString());
  }

  @Test
  public void testGetTotalPriceForDeviceTypeTest() {
    ArrayList<Integer> input = new ArrayList<Integer>();
    input.add(500);
    input.add(600);
    input.add(200);
    input.add(900);
    input.add(900);
    HashMap<String, BigDecimal> estimates = PriceReporter.getTotalPrice(input);
    assertEquals(estimates.size(), 2);
    assertEquals(new BigDecimal("0.88"), estimates.get("virtual"));
    assertEquals(new BigDecimal("4.42"), estimates.get("physical"));
  }

  @Test
  public void testGetTotalBillableTime() {
    ArrayList<Integer> input = new ArrayList<Integer>();
    input.add(30);
    input.add(50);
    input.add(73);
    input.add(9);
    Double totalBillableTime = PriceReporter.getTotalBillableTime(input);
    assertEquals(totalBillableTime.toString(), "5.0");
  }
}
