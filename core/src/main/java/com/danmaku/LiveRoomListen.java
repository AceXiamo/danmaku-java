package com.danmaku;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.danmaku.constants.Bilibili;
import com.danmaku.entity.*;
import com.danmaku.enums.MessageType;
import com.danmaku.enums.PacketTypeEnum;
import com.danmaku.enums.ProtocolVersionEnum;
import com.danmaku.service.*;
import com.danmaku.services.BiliRequest;
import com.danmaku.tools.BiliTool;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.regex.Matcher;

/**
 * The type Live room listen.
 *
 * @Author: AceXiamo
 * @ClassName: LiveRoomListen
 * @Date: 2023 /2/19 17:54
 */
@Slf4j
@Getter
public class LiveRoomListen {

    /**
     * The Room id.
     */
    private String roomId;
    /**
     * The Sequence.
     */
    private int sequence;
    /**
     * The Room info.
     */
    private JSONObject roomInfo;
    /**
     * The User info.
     */
    private JSONObject userInfo;

    /**
     * The Wls.
     */
    private WsListenService wls;
    /**
     * The Ws client.
     */
    private WebSocketClient wsClient;
    /**
     * The Request.
     */
    private ClientUpgradeRequest request;
    /**
     * The Session.
     */
    private Session session;
    /**
     * The Timer.
     */
    private Timer timer;
    /**
     * The Task.
     */
    private TimerTask task;

    /**
     * Instantiates a new Live room listen.
     *
     * @param roomId the room id
     */
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

    /**
     * Connect.
     */
    public void connect() {
        try {
            wsClient = new WebSocketClient();
            wsClient.start();
            wls = new WsListenService();
            request = new ClientUpgradeRequest();
            Future<Session> future = wsClient.connect(wls, new URI(Bilibili.LIVE_DANMU_WS), request);
            session = future.get();
            log.info("🟢 [ws连接已建立] - #{} {}", roomId, userInfo.getString("name"));
            var json = BiliTool.firstData(roomInfo.getString("room_id"));
            session.getRemote().sendBytes(BiliTool.packet(json.toJSONString(), PacketTypeEnum.VERIFY, sequence));
            // 心跳包任务
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    if (wls.isNotConnected()) {
                        timer.cancel();
                    } else {
                        sendHeartbeat();
                    }
                }
            };
            timer.schedule(task, 30 * 1000L, 30 * 1000L);
        } catch (Exception e) {
//            log.error(e.getMessage());
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
                        if (jsonObject == null || jsonObject.size() < 1) continue;
                        var cmd = jsonObject.getString("cmd");
                        if (MessageType.DANMU_MSG.getCode().equals(cmd)) {
                            // 当收到弹幕时接收到此条消息
                            danmuMsg(jsonObject);
                        }
//                        else if (MessageType.INTERACT_WORD.getCode().equals(cmd)) {
//                            // 有用户进入直播间或关注主播时触发
//                            interactWord(jsonObject);
//                        } else if (MessageType.SEND_GIFT.getCode().equals(cmd)) {
//                            // 用户投喂
//                            sendGift(jsonObject);
//                        } else if (MessageType.GUARD_BUY.getCode().equals(cmd)) {
//                            // 上舰
//                            guardBuy(jsonObject);
//                        } else if (MessageType.SUPER_CHAT_MESSAGE.getCode().equals(cmd)) {
//                            // 醒目留言
//                            superChatMessage(jsonObject);
//                        }
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
        try {
            sequence++;
            session.getRemote().sendBytes(BiliTool.packet(Bilibili.HEARTBEAT_DATA, PacketTypeEnum.HEARTBEAT, sequence));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Danmu msg.
     *
     * @param jsonObject the json object
     */
    private void danmuMsg(JSONObject jsonObject) {
        // 普通弹幕
        JSONArray info = jsonObject.getJSONArray("info");
        var msg = info.getString(1);
        var user = info.getJSONArray(2);
        var fans = info.getJSONArray(3);
        Danmakus danmakus = new Danmakus();
        danmakus.setRoomId(Integer.valueOf(roomId));
        danmakus.setContent(msg);
        danmakus.setUid(user.getString(0));
        danmakus.setUname(user.getString(1));
        if (fans != null && fans.size() > 0) {
            danmakus.setFansCardUid(fans.getInteger(fans.size() - 1));
            danmakus.setFansCardLevel(fans.getInteger(0));
            danmakus.setFansCardName(fans.getString(1));
            danmakus.setFansCardUname(fans.getString(3));
        }
//        SpringUtil.getBean(IDanmakusService.class).save(danmakus);
        log.info("[{}]{} : {}", danmakus.getUid(), danmakus.getUname(), danmakus.getContent());
    }

    /**
     * Interact word.
     * 用户进入直播间 / 关注主播
     *
     * @param jsonObject the json object
     */
    private void interactWord(JSONObject jsonObject) {
        var data = jsonObject.getJSONObject("data");
        var fans = data.getJSONObject("fans_medal");
        Interact interact = new Interact();
        interact.setType(data.getInteger("msg_type"));
        interact.setRoomId(Integer.valueOf(roomId));
        if (fans != null) {
            interact.setFansCardUid(fans.getInteger("target_id"));
            interact.setFansCardLevel(fans.getInteger("medal_level"));
            interact.setFansCardName(fans.getString("medal_name"));
            interact.setFansCardUname(fans.getString("anchor_uname"));
        }
        interact.setUid(data.getString("uid"));
        interact.setUname(data.getString("uname"));
        SpringUtil.getBean(IInteractService.class).save(interact);
    }

    /**
     * Send gift.
     * 赠送礼物
     *
     * @param jsonObject the json object
     */
    private void sendGift(JSONObject jsonObject) {
        var data = jsonObject.getJSONObject("data");
        var fans = data.getJSONObject("medal_info");
        Gift gift = new Gift();
        gift.setRoomId(Integer.valueOf(roomId));
        gift.setGiftId(data.getInteger("giftId"));
        gift.setGiftName(data.getString("giftName"));
        gift.setNum(data.getInteger("num"));
        gift.setDiscountPrice(data.getInteger("discount_price"));
        gift.setGiftPrice(data.getInteger("price"));
        gift.setUid(data.getString("uid"));
        gift.setUname(data.getString("uname"));
        if (fans != null) {
            gift.setFansCardUid(fans.getInteger("target_id"));
            gift.setFansCardLevel(fans.getInteger("medal_level"));
            gift.setFansCardName(fans.getString("medal_name"));
            gift.setFansCardUname(fans.getString("anchor_uname"));
        }
        SpringUtil.getBean(IGiftService.class).save(gift);
    }

    /**
     * Guard buy.
     * 上舰
     *
     * @param jsonObject the json object
     */
    private void guardBuy(JSONObject jsonObject) {
        var data = jsonObject.getJSONObject("data");
        GuardBuy guardBuy = new GuardBuy();
        guardBuy.setUid(data.getString("uid"));
        guardBuy.setUname(data.getString("username"));
        guardBuy.setGuardLevel(data.getInteger("guard_level"));
        guardBuy.setNum(data.getInteger("num"));
        guardBuy.setPrice(data.getInteger("price"));
        guardBuy.setGiftId(data.getInteger("gift_id"));
        guardBuy.setGiftName(data.getString("gift_name"));
        guardBuy.setRoomId(Integer.valueOf(roomId));
        SpringUtil.getBean(IGuardBuyService.class).save(guardBuy);
    }

    /**
     * Super chat message.
     * 醒目留言
     *
     * @param jsonObject the json object
     */
    private void superChatMessage(JSONObject jsonObject) {
        var data = jsonObject.getJSONObject("data");
        var user = data.getJSONObject("user_info");
        var fans = data.getJSONObject("medal_info");
        SuperChatMessage sc = new SuperChatMessage();
        sc.setUid(data.getString("uid"));
        sc.setUname(user.getString("uname"));
        sc.setRoomId(Integer.valueOf(roomId));
        if (fans != null) {
            sc.setFansCardUid(fans.getInteger("target_id"));
            sc.setFansCardUname(fans.getString("anchor_uname"));
            sc.setFansCardName(fans.getString("medal_name"));
            sc.setFansCardLevel(fans.getInteger("medal_level"));
        }
        sc.setPrice(data.getInteger("price"));
        sc.setTime(data.getInteger("time"));
        sc.setMessage(data.getString("message"));
        SpringUtil.getBean(ISuperChatMessageService.class).save(sc);
    }

    /**
     * The type Ws listen service.
     */
    public class WsListenService extends WebSocketAdapter {

        /**
         * Instantiates a new Ws listen service.
         */
        public WsListenService() {
            super();
        }

        @Override
        public void onWebSocketText(String message) {
            System.out.println("Received message: " + message);
        }

        @Override
        public void onWebSocketBinary(byte[] payload, int offset, int len) {
            msgHandle(ByteBuffer.wrap(payload));
        }

        @Override
        public void onWebSocketClose(int statusCode, String reason) {
            try {
//                log.error("🔴 [ws连接关闭] - {}", roomId);
//                log.error("🔴 [ws连接关闭，执行重连] - {} - {}", roomId, userInfo.getString("name"));
//                // 执行重连
//                timer.cancel();
//                task = null;
//                timer = null;
//                sequence = 0;
//                wls = null;
//                wsClient = null;
//                session = null;
//                request = null;
//                ThreadUtil.sleep(5000);
//                connect();
            } catch (Exception e) {
                log.error("connect error");
            }
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            log.error("ws连接错误 - {}", roomId);
        }

    }
}
