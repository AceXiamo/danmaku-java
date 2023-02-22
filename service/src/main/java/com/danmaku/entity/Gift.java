package com.danmaku.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("gift")
public class Gift {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 房间号
     */
    @TableField("room_id")
    private Integer roomId;

    /**
     * 礼物id
     */
    @TableField("gift_id")
    private Integer giftId;

    /**
     * 礼物名称
     */
    @TableField("gift_name")
    private String giftName;

    /**
     * 礼物价值
     */
    @TableField("gift_gold")
    private Integer giftGold;

    /**
     * 礼物价值(?
     */
    @TableField("gift_price")
    private Integer giftPrice;

    /**
     * 投喂数量
     */
    @TableField("num")
    private Integer num;

    /**
     * 投喂用户uid
     */
    @TableField("uid")
    private Integer uid;

    /**
     * 用户昵称
     */
    @TableField("uname")
    private String uname;

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
