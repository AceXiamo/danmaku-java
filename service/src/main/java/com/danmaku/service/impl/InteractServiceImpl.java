package com.danmaku.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.danmaku.entity.Interact;
import com.danmaku.mapper.InteractMapper;
import com.danmaku.service.IInteractService;
import org.springframework.stereotype.Service;

/**
 * @Author: AceXiamo
 * @ClassName: InteractServiceImpl
 * @Date: 2023/2/21 23:03
 */
@Service
public class InteractServiceImpl extends ServiceImpl<InteractMapper, Interact> implements IInteractService {
}
