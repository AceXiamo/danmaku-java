package com.danmaku.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.danmaku.entity.Danmakus;
import com.danmaku.mapper.DanmakusMapper;
import com.danmaku.service.IDanmakusService;
import org.springframework.stereotype.Service;

/**
 * @Author: AceXiamo
 * @ClassName: DanmakusServiceImpl
 * @Date: 2023/2/21 23:00
 */
@Service
public class DanmakusServiceImpl extends ServiceImpl<DanmakusMapper, Danmakus> implements IDanmakusService {
}
