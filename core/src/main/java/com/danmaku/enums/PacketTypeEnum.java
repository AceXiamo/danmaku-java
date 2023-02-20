package com.danmaku.enums;

import lombok.Getter;

/**
 * @Author: AceXiamo
 * @ClassName: PacketEnum
 * @Date: 2023/2/19 15:22
 */
@Getter
public enum PacketTypeEnum {

    HEARTBEAT("2", "心跳包"),
    HEARTBEAT_BACK("3", "心跳包回复（人气值）"),
    NORMAL("5", "普通包（命令）"),
    VERIFY("7", "认证包"),
    VERIFY_BACK("8", "认证包回复")
    ;

    private String code;
    private String desc;

    PacketTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
