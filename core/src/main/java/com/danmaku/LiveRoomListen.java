package com.danmaku;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.danmaku.constants.Bilibili;
import com.danmaku.entity.Danmakus;
import com.danmaku.entity.Gift;
import com.danmaku.entity.Interact;
import com.danmaku.enums.MessageType;
import com.danmaku.enums.PacketTypeEnum;
import com.danmaku.enums.ProtocolVersionEnum;
import com.danmaku.service.IDanmakusService;
import com.danmaku.service.IGiftService;
import com.danmaku.service.IInteractService;
import com.danmaku.services.BiliRequest;
import com.danmaku.tools.BiliTool;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
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
    private Timer timer;
    private TimerTask task;

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
        try {
            client = new WebSocketClient(new URI(Bilibili.LIVE_DANMU_WS)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    log.info("ws连接已建立 - #{} {}", roomId, userInfo.getString("name"));
                    var json = BiliTool.firstData(roomInfo.getString("room_id"));
                    client.send(BiliTool.packet(json.toJSONString(), PacketTypeEnum.VERIFY, sequence));

                    // 心跳包任务
                    timer = new Timer();
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            sendHeartbeat();
                        }
                    };
                    timer.schedule(task, 0, 30000);
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

    /**
     * Msg handle.
     *
     * @param buffer the buffer
     */
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
                        var cmd = jsonObject.getString("cmd");
                        if (MessageType.DANMU_MSG.getCode().equals(cmd)) {
                            // 当收到弹幕时接收到此条消息
                            danmuMsg(jsonObject);
                        } else if (MessageType.INTERACT_WORD.getCode().equals(cmd)) {
                            // 有用户进入直播间或关注主播时触发
                            interactWord(jsonObject);
                        } else if (MessageType.SEND_GIFT.getCode().equals(cmd)) {
                            // 用户投喂
                            sendGift(jsonObject);
                            var data = jsonObject.getJSONObject("data");
//                            discount_price
                            log.info("{} - {} - {}",
                                    data.getString("giftName"),
                                    data.getString("discount_price"),
                                    data.getString("price"));
                        } else if (MessageType.GUARD_BUY.getCode().equals(cmd)) {
                            guardBuy(jsonObject);
                        } else if (MessageType.SUPER_CHAT_MESSAGE.getCode().equals(cmd)) {
                            superChatMessage(jsonObject);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Send heartbeat.
     */
    public void sendHeartbeat() {
        log.info("send heartbeat - {}", userInfo.getString("name"));
        if (!client.isClosed()) {
            sequence++;
            client.send(BiliTool.packet(Bilibili.HEARTBEAT_DATA, PacketTypeEnum.HEARTBEAT, sequence));
        }
    }

    private void danmuMsg(JSONObject jsonObject) {
        // 普通弹幕
        JSONArray info = jsonObject.getJSONArray("info");
        var msg = info.getString(1);
        var user = info.getJSONArray(2);
        var fans = info.getJSONArray(3);
        Danmakus danmakus = new Danmakus();
        danmakus.setRoomId(Integer.valueOf(roomId));
        danmakus.setContent(msg);
        danmakus.setUid(user.getInteger(0));
        danmakus.setUname(user.getString(1));
        if (fans != null && fans.size() > 0) {
            danmakus.setFansCardUid(fans.getInteger(fans.size() - 1));
            danmakus.setFansCardLevel(fans.getInteger(0));
            danmakus.setFansCardName(fans.getString(1));
            danmakus.setFansCardUname(fans.getString(3));
        }
        SpringUtil.getBean(IDanmakusService.class).save(danmakus);
    }

    private void interactWord(JSONObject jsonObject) {
        // 用户进入直播间 / 关注主播
        var data = jsonObject.getJSONObject("data");
        var fans = data.getJSONObject("fans_medal");
        Interact interact = new Interact();
        interact.setType(data.getInteger("msg_type"));
        interact.setRoomId(Integer.valueOf(roomId));
        if (fans != null && fans.size() > 0) {
            interact.setFansCardUid(fans.getInteger("target_id"));
            interact.setFansCardLevel(fans.getInteger("medal_level"));
            interact.setFansCardName(fans.getString("medal_name"));
        }
        interact.setUid(data.getInteger("uid"));
        interact.setUname(data.getString("uname"));
        SpringUtil.getBean(IInteractService.class).save(interact);
    }

    private void sendGift(JSONObject jsonObject) {
        var data = jsonObject.getJSONObject("data");
        var fans = jsonObject.getJSONObject("medal_info");
        Gift gift = new Gift();
        gift.setRoomId(Integer.valueOf(roomId));
        gift.setGiftId(data.getInteger("giftId"));
        gift.setGiftName(data.getString("giftName"));
        gift.setNum(data.getInteger("num"));
        gift.setGiftGold(data.getInteger("gold"));
        gift.setGiftPrice(data.getInteger("price"));
        gift.setUid(data.getInteger("uid"));
        gift.setUname(data.getString("uname"));
        if (fans != null && fans.size() > 0) {
            gift.setFansCardUid(fans.getInteger("target_id"));
            gift.setFansCardLevel(fans.getInteger("medal_level"));
            gift.setFansCardName(fans.getString("medal_name"));
        }
        SpringUtil.getBean(IGiftService.class).save(gift);
    }

    private void superChatMessage(JSONObject jsonObject) {
        System.out.println(jsonObject.toJSONString());
    }

    private void guardBuy(JSONObject jsonObject) {
        System.out.println(jsonObject.toJSONString());
    }
}
