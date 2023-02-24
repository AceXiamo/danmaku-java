package com.danmaku.entity;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * The type Loaded live.
 *
 * @Author: AceXiamo
 * @ClassName: LoadedLive
 * @Date: 2023 /2/23 10:01
 */
@Data
public class LoadedLive {

    /**
     * The Room id.
     * 房间号
     */
    private String roomId;
    /**
     * The Room info.
     * 房间信息
     */
    private JSONObject roomInfo;
    /**
     * The User info.
     * 主播信息
     */
    private JSONObject userInfo;
    /**
     * The Status.
     * 连接状态（0: 正常 1: 已断开
     */
    private String status;

}
