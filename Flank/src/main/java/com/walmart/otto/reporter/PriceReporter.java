package com.walmart.otto.reporter;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceReporter {
    private static double virtualDevicePricePerMin = 1.00;
    private static double realDevicePricePerMin = 5.00;

    private static HashMap<String, Double> pricePerMinForDevice = new HashMap<String, Double>();
    private static HashMap<String, BigDecimal> estimates = new HashMap<String, BigDecimal>();

    public static HashMap<String, BigDecimal>  getTotalPrice(List<Integer> times)  {

        HashMap<String, Double> mapOfPrices = getPricePerMinForDevice();

        for (Map.Entry<String, Double> entry : mapOfPrices.entrySet()) {
            BigDecimal price = new BigDecimal(0.00);
            for(Integer executionTime: times) {
                try {
                    price = price.add(calculatePrice(executionTime, entry.getValue()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                estimates.put(entry.getKey(), price);
            }
        }
        return estimates;
    }

    public static Integer  getTotalBillabeTime(List<Integer> times)  {

        HashMap<String, Double> mapOfPrices = getPricePerMinForDevice();
        Integer time = 0;

        for(Integer executionTime: times) {
            time  = time + getBillableTime(executionTime);
        }

        return time;
    }

    public static BigDecimal calculatePrice(Number totalTimeInSecs, double pricePerMin) throws ParseException {

        int billableTime = getBillableTime(totalTimeInSecs);
        double actualPrice = billableTime * pricePerMin;

        BigDecimal a = new BigDecimal(actualPrice);
        BigDecimal roundOffPrice = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return roundOffPrice;
    }

    public static Integer getBillableTime(Number totalTimeInSecs){
        double time = totalTimeInSecs.doubleValue()/60;
        int roundedTime = (int) Math.ceil(time);
        return roundedTime;
    }

    private static HashMap getPricePerMinForDevice(){
        pricePerMinForDevice.put("virtual", virtualDevicePricePerMin);
        pricePerMinForDevice.put("physical", realDevicePricePerMin);
        return pricePerMinForDevice;
    }
}
