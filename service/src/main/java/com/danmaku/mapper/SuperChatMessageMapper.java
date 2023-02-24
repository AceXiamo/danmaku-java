package com.danmaku.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.danmaku.entity.SuperChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: AceXiamo
 * @ClassName: SuperChatMessageMapper
 * @Date: 2023/2/23 09:55
 */
@Mapper
public interface SuperChatMessageMapper extends BaseMapper<SuperChatMessage> {
}
