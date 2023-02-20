package com.danmaku.tools;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: AceXiamo
 * @ClassName: WSTool
 * @Date: 2023/2/18 16:27
 */
@Slf4j
public class WSTool {

    /**
     * The constant connects.
     * 保存已建立的连接
     */
    private static Map<String, WebSocketClient> connects = new HashMap<>();

    /**
     * New connect.
     * 增加一个 ws 连接
     *
     * @param key the key
     * @param url the url
     */
    public static WebSocketClient newConnect(String key, String url) {
        WebSocketClient client = null;
        try {
            CountDownLatch latch = new CountDownLatch(1);
            client = new WebSocketClient(new URI(url)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    log.info("ws连接已建立 - {}", key);
                    latch.countDown();
                }

                @Override
                public void onMessage(String s) {

                }

                @Override
                public void onMessage(ByteBuffer buffer) {
                    BiliTool.unPacket(buffer);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    log.info("ws连接已关闭 - {}", key);
                }

                @Override
                public void onError(Exception e) {
                    log.info("ws连接错误 - {}", key);
                }
            };
            client.connect();
            connects.put(key, client);
            latch.await();
        }catch (Exception e) {
            log.error(e.getMessage());
        }
        return client;
    }

    /**
     * Gets client.
     * 获取已建立连接
     *
     * @param uid the uid
     * @return the client
     */
    public static WebSocketClient getClient(String uid) {
        return connects.get(uid);
    }

}
