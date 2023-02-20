package com.danmaku.enums;

import lombok.Getter;

/**
 * @Author: AceXiamo
 * @ClassName: ProtocolVersionEnum
 * @Date: 2023/2/19 15:29
 */
@Getter
public enum ProtocolVersionEnum {

    ZERO("0", "普通包正文不使用压缩"),
    ONE("1", "心跳及认证包正文不使用压缩"),
    TWO("2", "普通包正文使用zlib压缩"),
    THREE("3", "普通包正文使用brotli压缩,解压为一个带头部的协议0普通包"),
    ;

    private String code;
    private String desc;

    ProtocolVersionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
