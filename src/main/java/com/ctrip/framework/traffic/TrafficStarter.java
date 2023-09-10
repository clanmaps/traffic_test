package com.ctrip.framework.traffic;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Created by jixinwang on 2023/9/6
 */
@SpringBootApplication
public class TrafficStarter extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TrafficStarter.class);
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(TrafficStarter.class).run(args);
    }
}
