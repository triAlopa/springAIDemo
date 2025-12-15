package com.chen.pojo.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * hr表
 * @TableName tb_ai_model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Model implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键自增
     */
    private Integer id;

    /**
     * 人物模型id
     */
    private Integer modelId;

    /**
     * 附属公司id 关联表
     */
    private String companyId;

    /**
     * hr的名称
     */
    private String name;

    /**
     * 描述用于ai提示词 --多场景发挥
     */
    private String description;

    /**
     * 模型的头像
     */
    private String image;

    /**
     * 用于ai风格 0~2.0 值越大越有创意
     */
    private Double temperature;

    /**
     * 用户开启会话的开场白
     */
    private String openMessage;

    /**
     * 模型的状态，0.表示禁用 1.开启
     */
    private Integer enable;

    /**
     * 逻辑删除 0.表示删除 1.正常
     */
    private Integer isDel;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 修改的用户id
     */
    private Integer updateUserId;


}