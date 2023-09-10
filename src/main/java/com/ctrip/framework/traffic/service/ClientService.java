package com.ctrip.framework.traffic.service;

import com.ctrip.framework.traffic.controller.ClientVO;
import com.ctrip.framework.traffic.netty.client.NettyClient;
import org.springframework.stereotype.Service;

/**
 * Created by jixinwang on 2023/9/7
 */
@Service
public class ClientService {

    private boolean worked = false;

    public synchronized void start(ClientVO clientVO) throws Exception {
        if (!worked) {
            NettyClient client = new NettyClient(clientVO);
            client.start();
            worked = true;
        } else {
            throw new Exception("client is working");
        }
    }
}
