package com.danmaku.services;

import cn.hutool.core.thread.ThreadUtil;
import com.danmaku.LiveRoomListen;
import com.danmaku.constants.Bilibili;
import com.danmaku.enums.PacketTypeEnum;
import com.danmaku.tools.BiliTool;
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

    /**
     * Send heartbeat.
     * 发送心跳包
     *
     * @param listen   the listen
     * @param sequence the sequence
     */
    @Async
    public void sendHeartbeat(LiveRoomListen listen, int sequence) {
        ThreadUtil.sleep(30 * 1000);
        log.info("send heartbeat");
        if (!listen.getClient().isClosed()) {
            sequence++;
            listen.getClient().send(BiliTool.packet(Bilibili.HEARTBEAT_DATA, PacketTypeEnum.HEARTBEAT, sequence));
            sendHeartbeat(listen, sequence);
        }
    }

}
