package com.danmaku.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class SuperChatMessage {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户uid
     */
    @TableField("uid")
    private Integer uid;

    /**
     * 用户昵称
     */
    @TableField("uname")
    private String uname;

    /**
     * 房间号
     */
    @TableField("room_id")
    private Integer roomId;

    /**
     * 粉丝牌主播uid
     */
    @TableField("fans_card_uid")
    private Integer fansCardUid;

    /**
     * 粉丝牌主播昵称
     */
    @TableField("fans_card_uname")
    private String fansCardUname;

    /**
     * 粉丝牌名称
     */
    @TableField("fans_card_name")
    private String fansCardName;

    /**
     * 粉丝牌等级
     */
    @TableField("fans_card_level")
    private Integer fansCardLevel;

    /**
     * sc金额（等比
     */
    @TableField("price")
    private Integer price;

    /**
     * sc持续时间
     */
    @TableField("time")
    private Integer time;

    /**
     * sc内容
     */
    @TableField("message")
    private String message;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
}

