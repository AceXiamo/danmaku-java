package com.danmaku.configuration;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.danmaku.entity.InitLive;
import com.danmaku.service.IInitLiveService;
import com.danmaku.services.LiveDanmu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: AceXiamo
 * @ClassName: ApplicationInit
 * @Date: 2023/2/23 11:13
 */
@Slf4j
@Component
public class ApplicationInit implements CommandLineRunner {

    @Autowired
    private IInitLiveService service;
    @Autowired
    private LiveDanmu danmu;

    private int page = 1;
    private int pageSize = 10;
    private int size = 0;
    private int time = 5;
    private long count = 0;
    private Timer timer;

    @Override
    public void run(String... args) {
        log.info("======================================================");
        log.info("开始加载已保存直播间 at {}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        log.info("======================================================");
        count = service.count();
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                LambdaQueryWrapper<InitLive> wrapper = new LambdaQueryWrapper<>();
                wrapper.orderByAsc(InitLive::getLoadSort);
                IPage<InitLive> iPage = new Page<>(page, pageSize);
                iPage = service.page(iPage, wrapper);
                size += iPage.getRecords().size();
                iPage.getRecords().forEach(v -> {
                    danmu.addListen(String.valueOf(v.getRoomId()));
                });
                if (size == count) {
                    log.info("======================================================");
                    log.info("加载已完成 at {}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
                    log.info("======================================================");
                    timer.cancel();
                } else {
                    page++;
                }
            }
        };
        timer.schedule(task, 0, time * 1000L);
    }
}
