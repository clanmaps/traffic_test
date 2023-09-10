package com.ctrip.framework.traffic.controller;

/**
 * Created by jixinwang on 2023/9/8
 */
public class ClientVO {

    private String serverHost;
    private int serverPort;
    private int bandWidth;
    private int period;

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

    @Override
    public String toString() {
        return "ClientVO{" +
                "serverHost='" + serverHost + '\'' +
                ", serverPort=" + serverPort +
                ", bandWidth=" + bandWidth +
                ", period=" + period +
                '}';
    }
}
