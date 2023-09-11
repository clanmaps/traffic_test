package com.ctrip.framework.traffic.service;

import com.ctrip.framework.traffic.controller.ClientVO;
import com.ctrip.framework.traffic.netty.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by jixinwang on 2023/9/7
 */
@Service
public class ClientService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private boolean worked = false;

    public synchronized void start(ClientVO clientVO) throws Exception {
        if (worked) {
            throw new Exception("client is working");
        }

        int parallel = clientVO.getParallel();
        logger.info("[client] start parallel {} ......", parallel);
        if (parallel < 0 || parallel > 1000) {
            throw new Exception("parallel should more then 0 and less then 1000");
        }

        for (int i = 0; i < parallel; i++) {
            start(clientVO, i);
        }
        worked = true;
    }

    private void start(ClientVO clientVO, int clientId) {
        NettyClient client = new NettyClient(clientVO);
        logger.info("[client] start client id {} ......", clientId);
        client.start(clientId);
    }
}
