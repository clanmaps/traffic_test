package com.ctrip.framework.traffic.controller;

import com.ctrip.framework.traffic.controller.utils.ApiResult;
import com.ctrip.framework.traffic.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by jixinwang on 2023/9/6
 */
@RestController
@RequestMapping("/api/traffic/client")
public class ClientController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ClientService service;

    @PostMapping
    public ApiResult<Boolean> start(@RequestBody ClientVO clientVO) {
        try {
            logger.info("[client] client start: {}", clientVO);
            service.start(clientVO);
            return ApiResult.getSuccessInstance(true);
        } catch (Exception e) {
            return ApiResult.getFailInstance(false, e.getMessage());
        }
    }

    @GetMapping
    public ApiResult<Boolean> query() {
        return null;
    }

    @DeleteMapping
    public ApiResult<Boolean> stop() {
        return null;
    }
}
