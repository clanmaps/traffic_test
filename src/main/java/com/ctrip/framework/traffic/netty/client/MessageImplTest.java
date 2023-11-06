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
        String filePath = "/Users/wjx/Downloads/flow/cen_sin_sha/11_03_sin_sha_delay.log";
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
        String filePath = "/Users/wjx/Downloads/flow/cen_sin_sha/11_03_sin_sha_result.csv";
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
                long maxRetrans = Long.parseLong(ret[8].trim());
                long retransRateOver5 = Long.parseLong(ret[9].trim());
                long retransRateOver10 = Long.parseLong(ret[10].trim());
                long retransRateOver50 = Long.parseLong(ret[11].trim());
                long retransRateOver70 = Long.parseLong(ret[12].trim());
                RetransEntry curRetransEntry = new RetransEntry(curTotalSend, curTotalRetrans, maxRetrans, retransRateOver5, retransRateOver10, retransRateOver50, retransRateOver70);

                if (pairs == null) {
                    RetransEntry end = new RetransEntry(curTotalSend, curTotalRetrans, maxRetrans, retransRateOver5, retransRateOver10, retransRateOver50, retransRateOver70);
                    RetransEntryPair pair = new RetransEntryPair(curRetransEntry, end);
                    pairs = Lists.newArrayList(pair);
                    portAndResults.put(port, pairs);
                } else {
                    RetransEntryPair lastPair = pairs.get(pairs.size() - 1);
                    long lastTotalSend = lastPair.getEnd().getTotalSend();

                    if (curTotalSend >= lastTotalSend) {
                        lastPair.end.setTotalSend(curTotalSend);
                        if (lastPair.end.getTotalRetrans() < curTotalRetrans) {
                            lastPair.end.setTotalRetrans(curTotalRetrans);
                        }

                        if (lastPair.end.getMaxRetrans() < maxRetrans) {
                            lastPair.end.setMaxRetrans(maxRetrans);
                        }
                        if (lastPair.end.getRetransRateOver5() < retransRateOver5) {
                            lastPair.end.setRetransRateOver5(retransRateOver5);
                        }
                        if (lastPair.end.getRetransRateOver10() < retransRateOver10) {
                            lastPair.end.setRetransRateOver10(retransRateOver10);
                        }
                        if (lastPair.end.getRetransRateOver50() < retransRateOver50) {
                            lastPair.end.setRetransRateOver50(retransRateOver50);
                        }
                        if (lastPair.end.getRetransRateOver70() < retransRateOver70) {
                            lastPair.end.setRetransRateOver70(retransRateOver70);
                        }
                    } else {
                        RetransEntry end = new RetransEntry(curTotalSend, curTotalRetrans, maxRetrans, retransRateOver5, retransRateOver10, retransRateOver50, retransRateOver70);
                        RetransEntryPair pair = new RetransEntryPair(curRetransEntry, end);
                        pairs.add(pair);
                    }
                }
            } catch (Exception e) {
                exception++;
                System.out.println("error: " + e);
                System.out.println(str);
            }
        }

        int size = portAndResults.size();
        long sumSend = 0;
        long sumRetrans = 0;
        long sumOver5 = 0;
        long sumOver10 = 0;
        long sumOver50 = 0;
        long sumOver70 = 0;
        long maxRetrans = 0;

        //avg
        for (List<RetransEntryPair> value : portAndResults.values()) {
            for (RetransEntryPair pair : value) {
                sumSend += pair.getEnd().getTotalSend() - pair.getStart().getTotalSend();
                sumRetrans += pair.getEnd().getTotalRetrans() - pair.getStart().getTotalRetrans();
                if (maxRetrans < pair.getEnd().getMaxRetrans()) {
                    maxRetrans = pair.getEnd().getMaxRetrans();
                }
                sumOver5 += pair.getEnd().getRetransRateOver5();
                sumOver10 += pair.getEnd().getRetransRateOver10();
                sumOver50 += pair.getEnd().getRetransRateOver50();
                sumOver70 += pair.getEnd().getRetransRateOver70();
            }

        }

        System.out.println("end-----------");
        System.out.println("count: " + count);
        System.out.println("size: " + size);
        System.out.println("ignore: " + ignore);
        System.out.println("exception: " + exception);
        System.out.println("maxRetrans: " + maxRetrans);
        System.out.println("sumRetrans: " + sumRetrans);
        System.out.println("sumSend: " + sumSend);
        double avgRetrans = (double) sumRetrans / sumSend;
        System.out.println("avgRetrans: " + avgRetrans);

        BigDecimal avg2Retrans = BigDecimal.valueOf(sumRetrans)
                .divide(BigDecimal.valueOf(sumSend), 8, BigDecimal.ROUND_HALF_UP);

        System.out.println("avg2Retrans: " + avg2Retrans);
        System.out.println("5: " + (double) sumOver5 / 80);
        System.out.println("10: " + (double) sumOver10 / 80);
        System.out.println("50: " + (double) sumOver50 / 80);
        System.out.println("70: " + (double) sumOver70 / 80);

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
        private long maxRetrans;
        private long retransRateOver5;
        private long retransRateOver10;
        private long retransRateOver50;
        private long retransRateOver70;

        public RetransEntry(long totalSend, long totalRetrans) {
            this.totalSend = totalSend;
            this.totalRetrans = totalRetrans;
        }

        public RetransEntry(long totalSend, long totalRetrans, long maxRetrans, long retransRateOver5,
                            long retransRateOver10, long retransRateOver50, long retransRateOver70) {
            this.totalSend = totalSend;
            this.totalRetrans = totalRetrans;
            this.maxRetrans = maxRetrans;
            this.retransRateOver5 = retransRateOver5;
            this.retransRateOver10 = retransRateOver10;
            this.retransRateOver50 = retransRateOver50;
            this.retransRateOver70 = retransRateOver70;
        }

        public long getMaxRetrans() {
            return maxRetrans;
        }

        public void setMaxRetrans(long maxRetrans) {
            this.maxRetrans = maxRetrans;
        }

        public long getRetransRateOver5() {
            return retransRateOver5;
        }

        public void setRetransRateOver5(long retransRateOver5) {
            this.retransRateOver5 = retransRateOver5;
        }

        public long getRetransRateOver10() {
            return retransRateOver10;
        }

        public void setRetransRateOver10(long retransRateOver10) {
            this.retransRateOver10 = retransRateOver10;
        }

        public long getRetransRateOver50() {
            return retransRateOver50;
        }

        public void setRetransRateOver50(long retransRateOver50) {
            this.retransRateOver50 = retransRateOver50;
        }

        public long getRetransRateOver70() {
            return retransRateOver70;
        }

        public void setRetransRateOver70(long retransRateOver70) {
            this.retransRateOver70 = retransRateOver70;
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