package com.ctrip.framework.traffic.utils;

/**
 * Created by jixinwang on 2023/9/8
 */
public class OsUtils {

    private static final int CPU_COUNT;

    static{

        String cpuCount = System.getProperty("CPU_COUNT");
        if( cpuCount != null){
            CPU_COUNT = Integer.parseInt(cpuCount);
        }else{
            CPU_COUNT = Runtime.getRuntime().availableProcessors();
        }
    }

    public static int getCpuCount(){
        return CPU_COUNT;
    }
}
