package com.danmaku.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.util.concurrent.*;

/**
 * The type Thread configuration.
 *
 * @Author: xiamo
 * @Description:
 * @ClassName: ThreadConfiguration
 * @Date: 2022 /7/31 22:14
 */
@Slf4j
@Configuration
public class StatisticsThreadConfiguration {

    /**
     * The Pool executor.
     */
    public ExecutorService poolExecutor;

    /**
     * Instantiates a new Thread configuration.
     */
    public StatisticsThreadConfiguration() {
        // 线程池
        poolExecutor =
                new ThreadPoolExecutor(20, 20, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * Destroy handle.
     */
    @PreDestroy
    public void destroyHandle() {
        poolExecutor.shutdown();
    }


}
