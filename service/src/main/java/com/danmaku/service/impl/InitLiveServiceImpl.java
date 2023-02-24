package com.danmaku.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.danmaku.entity.InitLive;
import com.danmaku.mapper.InitLiveMapper;
import com.danmaku.service.IInitLiveService;
import org.springframework.stereotype.Service;

/**
 * @Author: AceXiamo
 * @ClassName: InitLiveServiceImpl
 * @Date: 2023/2/23 09:57
 */
@Service
public class InitLiveServiceImpl extends ServiceImpl<InitLiveMapper, InitLive> implements IInitLiveService {
}
