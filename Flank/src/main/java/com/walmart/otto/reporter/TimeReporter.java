package com.walmart.otto.reporter;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeReporter {
    private static List<Integer> executionTimes = new ArrayList<>();

    public static void addExecutionTime(int time) {
        executionTimes.add(time);
    }

    public static int getCombinedExecutionTimes() {
        int sum = 0;

        for (int time : executionTimes) {
            sum = sum + time;
        }

        return sum;
    }

    public static String getEndTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
}