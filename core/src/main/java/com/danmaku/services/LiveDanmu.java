package com.danmaku.services;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.danmaku.LiveRoomListen;
import com.danmaku.entity.InitLive;
import com.danmaku.entity.LoadedLive;
import com.danmaku.service.IInitLiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Live danmu.
 *
 * @Author: AceXiamo
 * @ClassName: LiveDanmu
 * @Date: 2023 /2/18 17:45
 */
@Slf4j
@Component
public class LiveDanmu {

    public List<LiveRoomListen> lives = new ArrayList<>();

    @Autowired
    private IInitLiveService service;

    /**
     * Listen.
     * 创建ws连接
     *
     * @param roomId the room id
     */
    @Async
    public void addListen(String roomId) {
        boolean has = lives.stream().filter(v -> v.getRoomId().equals(roomId) || v.getRoomInfo().getString("short_id").equals(roomId)).findFirst().orElse(null) != null;
        if (has) {
            throw new RuntimeException("该房间已在监听列表中");
        }
        LiveRoomListen listen = new LiveRoomListen(roomId);
        listen.connect();
        lives.add(listen);

        // add to init
        LambdaQueryWrapper<InitLive> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InitLive::getRoomId, roomId);
        if (service.count(wrapper) < 1) {
            InitLive initLive = new InitLive();
            initLive.setUid(listen.getUserInfo().getString("mid"));
            initLive.setUname(listen.getUserInfo().getString("name"));
            initLive.setRoomId(Integer.valueOf(roomId));
            initLive.setLoadSort(0);
            SpringUtil.getBean(IInitLiveService.class).save(initLive);
        }
    }

    /**
     * List live list.
     *
     * @return the list
     */
    public List<LoadedLive> listLive() {
        List<LoadedLive> list = new ArrayList<>();
        lives.forEach(v -> {
            LoadedLive live = new LoadedLive();
            BeanUtil.copyProperties(v, live, CopyOptions.create().ignoreNullValue());
            live.setStatus(v.getWls().isNotConnected() ? "1" : "0");
            list.add(live);
        });
        return list;
    }

}
