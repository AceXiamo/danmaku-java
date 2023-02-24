package com.danmaku.web;

import cn.hutool.extra.spring.SpringUtil;
import com.danmaku.services.LiveDanmu;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Ws controller.
 *
 * @Author: AceXiamo
 * @ClassName: WsController
 * @Date: 2023 /2/18 16:43
 */
@RestController
@RequestMapping("ws")
public class WsController {

    /**
     * Add object.
     *
     * @param roomId the room id
     * @return the object
     */
    @GetMapping("add")
    public Object add(@RequestParam("roomId") String roomId) {
        try {
            SpringUtil.getBean(LiveDanmu.class).addListen(roomId);
            return "complete";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * List object.
     *
     * @return the object
     */
    @GetMapping("list")
    public Object list() {
        return SpringUtil.getBean(LiveDanmu.class).listLive();
    }

}
