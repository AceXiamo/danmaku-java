package com.danmaku.web;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.danmaku.configuration.StatisticsThreadConfiguration;
import com.danmaku.services.LiveDanmu;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

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

    @GetMapping("test")
    public Object test() {
        try {
            ExecutorService service = SpringUtil.getBean(StatisticsThreadConfiguration.class).poolExecutor;
            Queue<Map<String, Object>> queue = list2();
            CountDownLatch latch = new CountDownLatch(queue.size());
            for (Map<String, Object> stringObjectMap : queue) {
                if (!Boolean.parseBoolean(stringObjectMap.get("status").toString())) {
                    service.submit(() -> {
                        stringObjectMap.put("status", true);
//                        System.out.println(Thread.currentThread().getId() + " === " + stringObjectMap.get("id"));
                        ThreadUtil.sleep(Integer.parseInt(stringObjectMap.get("time").toString()));
                        add(stringObjectMap);
                        latch.countDown();
                    });
                }
            }
            latch.await();
        } catch (Exception e) {
            System.out.println(e);
        }
        return 233;
    }

    private List<Map<String, Object>> list = new ArrayList<>();
    public void add(Map<String, Object> res) {
        synchronized (list) {
            list.add(res);
            if (list.size() >= 10) {
                System.out.println("清空list");
                ThreadUtil.sleep(3000);
                System.out.println("实际清空：" + list.size());
                for (Map<String, Object> stringObjectMap : list) {
                    System.out.print(stringObjectMap.get("id") + " - ");
                }
                System.out.println("");
                list.clear();
            }
        }
    }

    public static Queue<Map<String, Object>> list2() {
        Queue<Map<String, Object>> queue = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < 100; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", "id" + i);
            map.put("time", i % 2 == 0 ? 1000 : 2000);
            map.put("status", false);
            queue.add(map);
        }
        return queue;
    }

}
