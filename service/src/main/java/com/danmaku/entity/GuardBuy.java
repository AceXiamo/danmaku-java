package com.danmaku.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class GuardBuy {
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
     * 大航海等级（1: 总督， 2: 提督 3: 舰长？
     */
    @TableField("guard_level")
    private Integer guardLevel;

    /**
     * 数量
     */
    @TableField("num")
    private Integer num;

    /**
     * 金额
     */
    @TableField("price")
    private Integer price;

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
     * 房间号
     */
    @TableField("room_id")
    private Integer roomId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
}

