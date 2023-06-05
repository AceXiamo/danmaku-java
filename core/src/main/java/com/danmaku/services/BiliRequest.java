package com.danmaku.services;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.danmaku.constants.Bilibili;
import com.danmaku.tools.WbiTool;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Bili request.
 *
 * @Author: AceXiamo
 * @ClassName: BiliRequest
 * @Date: 2023 /2/19 00:56
 */
@Slf4j
public class BiliRequest {

    /**
     * The constant buvid3.
     */
    public static String buvid3 = "";

    /**
     * Gets room info.
     * 获取直播间信息
     *
     * @param roomId the room id
     * @return the room info
     */
    public static JSONObject getRoomInfo(String roomId) {
        Map<String, Object> params = new HashMap<>();
        params.put("room_id", roomId);
        var res = HttpUtil.get(Bilibili.LIVE_ROOM_INFO, params);
        return JSON.parseObject(res.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Gets danmu info.
     * 获取弹幕ws key
     *
     * @param roomId the room id - 真实id（非短号
     * @return the danmu info
     */
    public static JSONObject getDanmuInfo(String roomId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", roomId);
        var res = HttpUtil.get(Bilibili.LIVE_DANMU_INFO, params);
        return JSON.parseObject(res.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Gets user info.
     * 获取用户信息
     *
     * @param uid the uid
     * @return the user info
     */
    public static JSONObject getUserInfo(String uid) {
        Map<String, Object> params = new HashMap<>();
        params.put("mid", uid);
        var httpRequest = WbiTool.wbiHandle(Bilibili.GET_USER_INFO, params);
        return JSON.parseObject(httpRequest.execute().body().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Gets buvid 3.
     *
     * @return the buvid 3
     */
    public static void loadBuvid3() {
        String url = "https://www.bilibili.com";
        HttpResponse resp = HttpRequest.get(url).execute();
        buvid3 = String.valueOf(resp.getCookie("buvid3"));
    }

}
