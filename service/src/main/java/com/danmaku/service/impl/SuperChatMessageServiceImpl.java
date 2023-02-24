package com.danmaku.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.danmaku.entity.SuperChatMessage;
import com.danmaku.mapper.SuperChatMessageMapper;
import com.danmaku.service.ISuperChatMessageService;
import org.springframework.stereotype.Service;

/**
 * @Author: AceXiamo
 * @ClassName: SuperChatMessageServiceImpl
 * @Date: 2023/2/23 09:58
 */
@Service
public class SuperChatMessageServiceImpl extends ServiceImpl<SuperChatMessageMapper, SuperChatMessage> implements ISuperChatMessageService {
}
