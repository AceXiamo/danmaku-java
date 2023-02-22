package com.danmaku.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.danmaku.entity.Gift;
import com.danmaku.mapper.GiftMapper;
import com.danmaku.service.IGiftService;
import org.springframework.stereotype.Service;

/**
 * @Author: AceXiamo
 * @ClassName: GiftServiceImpl
 * @Date: 2023/2/21 23:03
 */
@Service
public class GiftServiceImpl extends ServiceImpl<GiftMapper, Gift> implements IGiftService {
}
