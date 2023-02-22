package com.danmaku.enums;

import lombok.Getter;

/**
 * @Author: AceXiamo
 * @ClassName: MessageType
 * @Date: 2023/2/21 23:05
 */
@Getter
public enum MessageType {

    DANMU_MSG("DANMU_MSG", "当收到弹幕时接收到此条消息"),
    INTERACT_WORD("INTERACT_WORD", "有用户进入直播间或关注主播时触发"),
    SEND_GIFT("SEND_GIFT", "用户投喂"),
    GUARD_BUY("GUARD_BUY", "上舰"),
    SUPER_CHAT_MESSAGE("SUPER_CHAT_MESSAGE", "醒目留言"),
    ;

    private String code;
    private String desc;

    MessageType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
