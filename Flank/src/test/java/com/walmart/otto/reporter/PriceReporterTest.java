package com.walmart.otto.reporter;

import org.junit.Test;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class PriceReporterTest {
    @Test
    public void testCalculatePrice() throws ParseException {

        BigDecimal price1 = PriceReporter.calculatePrice(125, 5);
        assertEquals(price1.toString(), "0.25");

        BigDecimal price2 = PriceReporter.calculatePrice(67, 1);
        assertEquals(price2.toString(), "0.03");

        BigDecimal price3 = PriceReporter.calculatePrice(60, 1);
        assertEquals(price3.toString(), "0.02");

        BigDecimal price4 = PriceReporter.calculatePrice(60, 5);
        assertEquals(price4.toString(), "0.08");

        BigDecimal price5 = PriceReporter.calculatePrice(120, 5);
        assertEquals(price5.toString(), "0.17");

        BigDecimal price6 = PriceReporter.calculatePrice(123.56, 5);
        assertEquals(price6.toString(), "0.25");

        BigDecimal price7 = PriceReporter.calculatePrice(123.56, 1);
        assertEquals(price7.toString(), "0.05");

        BigDecimal price8 = PriceReporter.calculatePrice(123.5623344233434343, 1);
        assertEquals(price8.toString(), "0.05");

        BigDecimal price9 = PriceReporter.calculatePrice(0, 1);
        assertEquals(price9.toString(), "0.00");

        BigDecimal price10 = PriceReporter.calculatePrice(0.45, 5);
        assertEquals(price10.toString(), "0.08");

        BigDecimal price11 = PriceReporter.calculatePrice(0.599999, 5);
        assertEquals(price11.toString(), "0.08");

        BigDecimal price12 = PriceReporter.calculatePrice(0.599999, 1);
        assertEquals(price12.toString(), "0.02");

        BigDecimal price13 = PriceReporter.calculatePrice(-0.599999, 1);
        assertEquals(price13.toString(), "0.00");
    }

    @Test
    public void testGetTotalPriceForDeviceTypeTest(){
        ArrayList<Integer> input = new ArrayList<Integer>();
        input.add(30);
        input.add(50);
        input.add(73);
        input.add(9);
        HashMap<String, BigDecimal> estimates = PriceReporter.getTotalPrice(input);
        assertEquals(estimates.size(), 2);
        assertEquals(estimates.get("virtual"), new BigDecimal("0.09"));
        assertEquals(estimates.get("physical"), new BigDecimal("0.41"));
    }

    @Test
    public void testGetTotalBillableTime(){
        ArrayList<Integer> input = new ArrayList<Integer>();
        input.add(30);
        input.add(50);
        input.add(73);
        input.add(9);
        Double totalBillableTime = PriceReporter.getTotalBillableTime(input);
        assertEquals(totalBillableTime.toString(), "5.0");
    }
}