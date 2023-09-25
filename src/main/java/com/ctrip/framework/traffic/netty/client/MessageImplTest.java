package com.ctrip.framework.traffic.netty.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.math.Stats;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
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
        String filePath = "/Users/wjx/Downloads/flow/m_sha_sin/0922_result2.csv";
        BufferedReader in = new BufferedReader(new FileReader(filePath));

        Map<Integer, List<RetransEntryPair>> portAndResults = Maps.newHashMap();

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
            List<RetransEntryPair> pairs = portAndResults.get(port);

            try {
                long curTotalSend = Long.parseLong(ret[4].trim());
                long curTotalRetrans = Long.parseLong(ret[2].trim());
                RetransEntry curRetransEntry = new RetransEntry(curTotalSend, curTotalRetrans);

                if (pairs == null) {
                    RetransEntry end = new RetransEntry(curTotalSend, curTotalRetrans);
                    RetransEntryPair pair = new RetransEntryPair(curRetransEntry, end);
                    pairs = Lists.newArrayList(pair);
                    portAndResults.put(port, pairs);
                } else {
                    RetransEntryPair lastPair = pairs.get(pairs.size() - 1);
                    long lastTotalSend = lastPair.getEnd().getTotalSend();

                    if (curTotalSend >= lastTotalSend) {
                        lastPair.end.setTotalSend(curTotalSend);
                        long lastTotalRetrans = lastPair.getEnd().getTotalRetrans();
                        if (curTotalRetrans > lastTotalRetrans) {
                            lastPair.end.setTotalRetrans(curTotalRetrans);
                        }
                    } else {
                        RetransEntry end = new RetransEntry(curTotalSend, curTotalRetrans);
                        RetransEntryPair pair = new RetransEntryPair(curRetransEntry, end);
                        pairs.add(pair);
                    }
                }
            } catch (Exception e) {
                exception++;
//                System.out.println("error: " + e);
//                System.out.println(str);
            }
        }

        int size = portAndResults.size();
        long sumSend = 0;
        long sumRetrans = 0;
//        long sumOver5 = 0;
//        long sumOver10 = 0;
//        long sumOver50 = 0;
//        long sumOver70 = 0;

        for (List<RetransEntryPair> value : portAndResults.values()) {
            for (RetransEntryPair pair : value) {
                sumSend += pair.getEnd().getTotalSend() - pair.getStart().getTotalSend();
                sumRetrans += pair.getEnd().getTotalRetrans() - pair.getStart().getTotalRetrans();
            }

        }

        System.out.println("end-----------");
        System.out.println("count: " + count);
        System.out.println("size: " + size);
        System.out.println("ignore: " + ignore);
        System.out.println("exception: " + exception);
//        System.out.println("max: " + max);
        System.out.println("sumRetrans: " + sumRetrans);
        System.out.println("sumSend: " + sumSend);
        double avg = (double) sumRetrans / sumSend;
        System.out.println("avg: " + avg);

        BigDecimal avg2 = BigDecimal.valueOf(sumRetrans)
                .divide(BigDecimal.valueOf(sumSend), 8, BigDecimal.ROUND_HALF_UP);

        System.out.println("avg2: " + avg2);
//        System.out.println("5: " + sumOver5 / 80);
//        System.out.println("10: " + sumOver10 / 80);
//        System.out.println("50: " + sumOver50 / 80);
//        System.out.println("70: " + sumOver70 / 80);

    }

    static class RetransEntryPair {
        private RetransEntry start;
        private RetransEntry end;

        public RetransEntryPair(RetransEntry start, RetransEntry end) {
            this.start = start;
            this.end = end;
        }

        public RetransEntry getStart() {
            return start;
        }

        public void setStart(RetransEntry start) {
            this.start = start;
        }

        public RetransEntry getEnd() {
            return end;
        }

        public void setEnd(RetransEntry end) {
            this.end = end;
        }
    }

    static class RetransEntry {
        private long totalSend;
        private long totalRetrans;

        public RetransEntry(long totalSend, long totalRetrans) {
            this.totalSend = totalSend;
            this.totalRetrans = totalRetrans;
        }

        public long getTotalSend() {
            return totalSend;
        }

        public void setTotalSend(long totalSend) {
            this.totalSend = totalSend;
        }

        public long getTotalRetrans() {
            return totalRetrans;
        }

        public void setTotalRetrans(long totalRetrans) {
            this.totalRetrans = totalRetrans;
        }
    }
}