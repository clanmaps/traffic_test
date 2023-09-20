package com.ctrip.framework.traffic.controller;

/**
 * Created by jixinwang on 2023/9/8
 */
public class ClientVO {

    private String serverHost;
    private int serverPort;

    //default bandwidth is 1 Mbps
    private int bandWidth = 1;

    //default send date period is 200 milliseconds
    private int period = 200;

    //default netty client number is 1
    private int parallel = 1;

    //default warm up time is 30 seconds
    private int omit = 30;

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getBandWidth() {
        return bandWidth;
    }

    public void setBandWidth(int bandWidth) {
        this.bandWidth = bandWidth;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getParallel() {
        return parallel;
    }

    public void setParallel(int parallel) {
        this.parallel = parallel;
    }

    public int getOmit() {
        return omit;
    }

    public void setOmit(int omit) {
        this.omit = omit;
    }

    public void check() throws Exception {
        if (bandWidth < 1 || bandWidth > 1000) {
            throw new Exception("client bandWidth should in [1, 1000] Mbps");
        }

        if (period < 100 || period > 1000 || period % 100 != 0) {
            throw new Exception("client period should in [100, 1000] milliseconds and a multiple of 100");
        }

        if (parallel < 1 || parallel > 500) {
            throw new Exception("client parallel should in [1, 500]");
        }

        if (omit < 0 || omit > 600) {
            throw new Exception("client warm up to omit should in [0, 600] seconds");
        }
    }

    @Override
    public String toString() {
        return "ClientVO{" +
                "serverHost='" + serverHost + '\'' +
                ", serverPort=" + serverPort +
                ", bandWidth=" + bandWidth +
                ", period=" + period +
                ", parallel=" + parallel +
                ", omit=" + omit +
                '}';
    }
}
