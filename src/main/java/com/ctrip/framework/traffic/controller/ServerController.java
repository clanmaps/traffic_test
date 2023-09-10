package com.ctrip.framework.traffic.controller;

import com.ctrip.framework.traffic.controller.utils.ApiResult;
import com.ctrip.framework.traffic.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by jixinwang on 2023/9/6
 */
@RestController
@RequestMapping("/api/traffic/server")
public class ServerController {

    @Autowired
    private ServerService service;


    @PostMapping
    public ApiResult<Boolean> start(@RequestBody ServerVO serverVO) {
        try {
            service.start(serverVO);
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
