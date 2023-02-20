package com.danmaku;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import com.danmaku.constants.Bilibili;
import com.danmaku.enums.PacketTypeEnum;
import com.danmaku.enums.ProtocolVersionEnum;
import com.danmaku.services.BiliRequest;
import com.danmaku.services.LiveDanmu;
import com.danmaku.tools.BiliTool;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;

/**
 * @Author: AceXiamo
 * @ClassName: LiveRoomListen
 * @Date: 2023/2/19 17:54
 */
@Slf4j
@Getter
public class LiveRoomListen {

    private String roomId;
    private int sequence;
    private JSONObject roomInfo;
    private JSONObject userInfo;
    private WebSocketClient client;

    public LiveRoomListen(String roomId) {
        this.roomId = roomId;
        sequence = 0;
        // 房间信息
        var res = BiliRequest.getRoomInfo(roomId);
        roomInfo = res.getJSONObject("data");
        // 主播信息
        var userInfoRes = BiliRequest.getUserInfo(roomInfo.getString("uid"));
        userInfo = userInfoRes.getJSONObject("data");
    }

    public void connect() {
        var listen = this;
        try {
            client = new WebSocketClient(new URI(Bilibili.LIVE_DANMU_WS)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    log.info("ws连接已建立 - #{} {}", roomId, userInfo.getString("name"));
                    var json = BiliTool.firstData(roomInfo.getString("room_id"));
                    client.send(BiliTool.packet(json.toJSONString(), PacketTypeEnum.VERIFY, sequence));
                    SpringUtil.getBean(LiveDanmu.class).sendHeartbeat(listen, sequence);
                }

                @Override
                public void onMessage(String s) {
                }

                @Override
                public void onMessage(ByteBuffer buffer) {
                    msgHandle(buffer);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    log.info("ws连接已关闭 - {}", roomId);
                }

                @Override
                public void onError(Exception e) {
                    log.info("ws连接错误 - {}", roomId);
                }
            };
            client.connect();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void msgHandle(ByteBuffer buffer) {
        try {
            // 协议版本校验
            String version = String.valueOf(buffer.getShort(6));
            byte[] content = new byte[buffer.limit() - BiliTool.HEAD_LENGTH];
            for (int i = 0; i < content.length; i++) {
                content[i] = buffer.get(i + BiliTool.HEAD_LENGTH);
            }
            if (ProtocolVersionEnum.ZERO.getCode().equals(version)) {

            } else if (ProtocolVersionEnum.ONE.getCode().equals(version)) {

            } else if (ProtocolVersionEnum.TWO.getCode().equals(version)) {

            } else if (ProtocolVersionEnum.THREE.getCode().equals(version)) {
                // Brotli Handle
                var res = BiliTool.decompress(content);
                var str = new String(res, StandardCharsets.UTF_8);
                Matcher matcher = BiliTool.jsonPattern.matcher(str);
                while (matcher.find()) {
                    String jsonString = matcher.group();
                    try {
                        JSONObject jsonObject = BiliTool.extractJsonObject(jsonString);
                        System.out.println(jsonObject.toJSONString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
