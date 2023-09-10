package com.ctrip.framework.traffic.service;

import com.ctrip.framework.traffic.controller.ServerVO;
import com.ctrip.framework.traffic.netty.server.NettyServer;
import org.springframework.stereotype.Service;

/**
 * Created by jixinwang on 2023/9/7
 */
@Service
public class ServerService {

    private boolean worked = false;

    public synchronized void start(ServerVO serverVO) throws Exception {
        if (!worked) {
            NettyServer server = new NettyServer(serverVO);
            server.start();
            worked = true;
        } else {
            throw new Exception("server is working");
        }
    }
}
