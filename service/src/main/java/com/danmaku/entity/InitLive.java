package com.danmaku.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class InitLive {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 房间号
     */
    @TableField("room_id")
    private Integer roomId;

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
     * 加载排序（0: 最高优先级
     */
    @TableField("load_sort")
    private Integer loadSort;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
}

