package com.danmaku.constants;

/**
 * @Author: AceXiamo
 * @ClassName: BilibiliApi
 * @Date: 2023/2/18 17:41
 */
public class Bilibili {

    /* 弹幕ws地址 */
    public static final String LIVE_DANMU_WS = "wss://broadcastlv.chat.bilibili.com:2245/sub";

    /* 心跳包数据 */
    public static final String HEARTBEAT_DATA = "[object Object]";

    /* 获取直播间信息 */
    public static final String LIVE_ROOM_INFO = "https://api.live.bilibili.com/room/v1/Room/get_info";

    /* 获取信息流认证密钥 */
    public static final String LIVE_DANMU_INFO = "https://api.live.bilibili.com/xlive/web-room/v1/index/getDanmuInfo";

    /* 获取用户信息 */
    public static final String GET_USER_INFO = "https://api.bilibili.com/x/space/wbi/acc/info";
    
}
