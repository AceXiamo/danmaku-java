package com.danmaku.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.springframework.http.HttpHeaders;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: AceXiamo
 * @ClassName: WbiTool
 * @Date: 2023/6/5 00:43
 */
public class WbiTool {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String REFERER = "https://www.bilibili.com";
    private static String wbiStr = "";

    private static String getMixinKey(int[] ae) {
        int[] oe = {46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49, 33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41,
                13, 37, 48, 7, 16, 24, 55, 40, 61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11, 36, 20, 34, 44, 52};
        StringBuilder le = new StringBuilder();
        for (int i : oe) {
            le.append((char) ae[i]);
        }
        return le.substring(0, 32);
    }

    private static String md5(String s) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(s.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static String split(String s) {
        String[] parts = s.split("/");
        String fileName = parts[parts.length - 1];
        String[] fileNameParts = fileName.split("\\.");
        return fileNameParts[0];
    }

    public static void loadWbi() {
        HttpResponse response = HttpRequest.get("https://api.bilibili.com/x/web-interface/nav")
                .header(HttpHeaders.USER_AGENT, USER_AGENT)
                .header(HttpHeaders.REFERER, REFERER)
                .execute();
        wbiStr = response.body();
    }

    private static Map<String, Object> encWbi(Map<String, Object> params) {
        try {
            if (StrUtil.isEmpty(wbiStr)) loadWbi();
            int startIndex = wbiStr.indexOf("\"img_url\":\"") + 11;
            int endIndex = wbiStr.indexOf("\",\"sub_url\":\"");
            String img_url = wbiStr.substring(startIndex, endIndex);
            startIndex = endIndex + 14;
            endIndex = wbiStr.indexOf("\"}", startIndex);
            String sub_url = wbiStr.substring(startIndex, endIndex);

            String img_value = split(img_url);
            String sub_value = split(sub_url);
            String me = getMixinKey(concatArrays(convertStringToArray(img_value), convertStringToArray(sub_value)));
            long wts = System.currentTimeMillis() / 1000;
            params.put("wts", String.valueOf(wts));
            String[] sortedParams = params.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .toArray(String[]::new);
            String Ae = String.join("&", sortedParams);
            String w_rid = md5(Ae + me);

            Map<String, Object> result = new HashMap<>();
            result.put("w_rid", w_rid);
            result.put("wts", String.valueOf(wts));
            result.putAll(params);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int[] convertStringToArray(String s) {
        int[] array = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            array[i] = (int) s.charAt(i);
        }
        return array;
    }

    private static int[] concatArrays(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static HttpRequest wbiHandle(String api, Map<String, Object> params) {
        StringBuilder url = new StringBuilder(api);
        url.append("?");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            url.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        Map<String, Object> res = encWbi(params);
        url.append("w_rid=").append(res.get("w_rid")).append("&");
        url.append("wts=").append(res.get("wts"));
        HttpRequest request = HttpRequest.get(url.toString())
                .header(HttpHeaders.USER_AGENT, USER_AGENT)
                .header(HttpHeaders.REFERER, REFERER);
        return request;
    }

    public static void main(String[] args) {
        String api = "https://api.bilibili.com/x/space/wbi/acc/info";
        Map<String, Object> url_params = new HashMap<>();
        url_params.put("mid", "1637486568");
        HttpRequest request = wbiHandle(api, url_params);
        String body = request.execute().body();
        System.out.println(body);
    }

}
