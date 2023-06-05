package com.danmaku.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("danmakus")
public class Danmakus {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户uid
     */
    @TableField("uid")
    private String uid;

    /**
     * 用户昵称
     */
    @TableField("uname")
    private String uname;

    /**
     * 弹幕内容
     */
    @TableField("content")
    private String content;

    /**
     * 直播间房间号
     */
    @TableField("room_id")
    private Integer roomId;

    /**
     * 本次直播标题
     */
    @TableField("room_title")
    private String roomTitle;

    /**
     * 粉丝牌主播uid
     */
    @TableField("fans_card_uid")
    private Integer fansCardUid;

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
     * 粉丝牌主播昵称
     */
    @TableField("fans_card_uname")
    private String fansCardUname;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
}

