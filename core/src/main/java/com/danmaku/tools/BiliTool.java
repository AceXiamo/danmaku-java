package com.danmaku.tools;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.danmaku.enums.PacketTypeEnum;
import com.danmaku.enums.ProtocolVersionEnum;
import org.brotli.dec.BrotliInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: AceXiamo
 * @ClassName: ByteTool
 * @Date: 2023/2/19 15:18
 */
public class BiliTool {

    /* 头部长度 - 固定值 */
    public static final Integer HEAD_LENGTH = 16;

    /* 用于解压后提取json */
    public static final Pattern jsonPattern = Pattern.compile("\\{(?:[^{}]|\\{(?:[^{}]|\\{(?:[^{}]|\\{[^{}]*\\})*\\})*\\})*\\}");

    /**
     * First data json object.
     * 连接上ws后发送的第一条消息
     *
     * @param roomId the room id
     * @return the json object
     */
    public static JSONObject firstData(String roomId) {
        JSONObject json = new JSONObject();
        json.put("uid", 0);
        json.put("roomid", Integer.valueOf(roomId));
        json.put("protover", 3);
        json.put("platform", "web");
        return json;
    }

    /**
     * Packet byte buffer.
     * 封包
     *
     * @param jsonStr    the jsonStr
     * @param packetType the packet type
     * @param sequence   the sequence
     * @return the byte buffer
     */
    public static ByteBuffer packet(String jsonStr, PacketTypeEnum packetType, int sequence) {
        int len = jsonStr.getBytes().length + HEAD_LENGTH;
        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer.putInt(len);
        buffer.putShort(HEAD_LENGTH.shortValue());
        buffer.putShort(Short.valueOf(ProtocolVersionEnum.ZERO.getCode()));
        buffer.putInt(Integer.valueOf(packetType.getCode()));
        buffer.putInt(sequence);
        buffer.put(jsonStr.getBytes());
        buffer.position(0);
        return buffer;
    }

    public static void unPacket(ByteBuffer buffer) {
        try {
            // 协议版本校验
            String version = String.valueOf(buffer.getShort(6));
            byte[] content = new byte[buffer.limit() - HEAD_LENGTH];
            for (int i = 0; i < content.length; i++) {
                content[i] = buffer.get(i + HEAD_LENGTH);
            }
            if (ProtocolVersionEnum.ZERO.getCode().equals(version)) {

            } else if (ProtocolVersionEnum.ONE.getCode().equals(version)) {

            } else if (ProtocolVersionEnum.TWO.getCode().equals(version)) {

            } else if (ProtocolVersionEnum.THREE.getCode().equals(version)) {
                var res = decompress(content);
                var str = new String(res, StandardCharsets.UTF_8);
                json(str);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(compressedData);
        BrotliInputStream brotliIn = new BrotliInputStream(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = brotliIn.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        out.close();
        brotliIn.close();
        return out.toByteArray();
    }

    public static JSONObject json(String str) {
        Matcher matcher = jsonPattern.matcher(str);
        while (matcher.find()) {
            String jsonString = matcher.group();
            try {
                JSONObject jsonObject = extractJsonObject(jsonString);
                System.out.println(jsonObject.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static JSONObject extractJsonObject(String jsonString) {
        if (jsonString.startsWith("{") && jsonString.endsWith("}")) {
            // 这里可能包裹两层打括号，挺离谱的
            try {
                return JSON.parseObject(jsonString);
            }catch (Exception e) {
                jsonString = jsonString.substring(1, jsonString.length() - 1);
                return JSON.parseObject(jsonString);
            }
        } else {
            return null;
        }
    }

}
