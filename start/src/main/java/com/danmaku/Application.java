package com.danmaku;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * The type Application.
 *
 * @Author: AceXiamo
 * @ClassName: Application
 * @Date: 2023 /2/18 16:16
 */
@EnableAsync
@SpringBootApplication
public class Application {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
