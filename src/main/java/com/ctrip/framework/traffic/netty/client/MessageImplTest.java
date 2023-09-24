package com.ctrip.framework.traffic.netty.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.math.Stats;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by jixinwang on 2023/9/23
 */
public class MessageImplTest {

    @Test
    public void testDelay() throws IOException {
        String filePath = "/Users/wjx/Downloads/flow/t_sha_sin/0924_delay.log";
        long delayAlert = 5 * 1000;


        BufferedReader in = new BufferedReader(new FileReader(filePath));
        String str;
        int over5 = 0;
        long max = 0;
        List<Long> delays = Lists.newArrayList();
        while ((str = in.readLine()) != null) {
            String[] ret = str.split(",");
            String delayStr = ret[1];
            long delay = Long.parseLong(delayStr);
            delays.add(delay);

            if (delay > max) {
                max = delay;
            }
            if (delay > delayAlert) {
                over5++;
            }
        }

        long avg = Math.round(Stats.meanOf(delays));
        System.out.println("avg:" + avg);
        System.out.println("max:" + max);
        System.out.println("over 5 seconds:" + over5);
        System.out.println("size:" + delays.size());
    }

    @Test
    public void testRetrans() throws IOException {
        String filePath = "/Users/wjx/Downloads/flow/t_sha_sin/0924_result2.csv";
        BufferedReader in = new BufferedReader(new FileReader(filePath));

        Map<Integer, String> portAndResult = Maps.newHashMap();

        String header = in.readLine();
        System.out.println(header);
        String str;
        int ignore = 0;
        int exception = 0;
        long count = 0;
        while ((str = in.readLine()) != null) {
            count++;
            String[] ret = str.split(",");
            String portStr = ret[1].trim();
            int port = Integer.parseInt(portStr);

            String totalStr = ret[4].trim();
            try {
                long total = Long.parseLong(totalStr);
                long lastTotal = 0;
                String lastStr = portAndResult.get(port);
                if (lastStr != null) {
                    lastTotal = Long.parseLong(lastStr.split(",")[4].trim());
                }

                if (total < lastTotal) {
                    ignore++;
//                    System.out.println("ignore:");
//                    System.out.println(str);
                    continue;
                }

                portAndResult.put(port, str);
            } catch (Exception e) {
                exception++;
//                System.out.println("error: " + e);
//                System.out.println(str);
            }
        }

        int size = portAndResult.size();
        long sumAvg = 0;
        long max = 0;
        long sumOver5 = 0;
        long sumOver10 = 0;
        long sumOver50 = 0;
        long sumOver70 = 0;

        for (String value : portAndResult.values()) {
            String[] ret = value.split(",");
//            System.out.println("fields: " + ret.length);
            String avgRetransRate = ret[7];
            long curAvg = Long.parseLong(avgRetransRate.trim());
//            if (curAvg > 1000) {
//                System.out.println(curAvg + " : " + value);
//            }

            sumAvg += curAvg;

            String maxRetransRate = ret[8];
            long curMax = Long.parseLong(maxRetransRate.trim());
            if (curMax > max) {
                max = curMax;
            }

            String retransRateOver5 = ret[9];
            sumOver5 += Long.parseLong(retransRateOver5.trim());

            String retransRateOver10 = ret[10];
            sumOver10 += Long.parseLong(retransRateOver10.trim());

            String retransRateOver50 = ret[11];
            sumOver50 += Long.parseLong(retransRateOver50.trim());

            String retransRateOver70 = ret[12];
            sumOver70 += Long.parseLong(retransRateOver70.trim());
        }

        System.out.println("end-----------");
        System.out.println("count: " + count);
        System.out.println("size: " + size);
        System.out.println("ignore: " + ignore);
        System.out.println("exception: " + exception);
        System.out.println("max: " + max);
        System.out.println("avg: " + sumAvg / size);
        System.out.println("5: " + sumOver5 / 80);
        System.out.println("10: " + sumOver10 / 80);
        System.out.println("50: " + sumOver50 / 80);
        System.out.println("70: " + sumOver70 / 80);

    }
}