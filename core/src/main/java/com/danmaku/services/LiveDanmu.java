package com.danmaku.services;

import com.danmaku.LiveRoomListen;
import lombok.extern.slf4j.Slf4j;
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

    public List<LiveRoomListen> list = new ArrayList<>();

    /**
     * Listen.
     * 创建ws连接
     *
     * @param roomId the room id
     */
    @Async
    public void addListen(String roomId) {
        LiveRoomListen listen = new LiveRoomListen(roomId);
        listen.connect();
        list.add(listen);
    }

}
