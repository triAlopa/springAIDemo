package com.chen.pojo.vo;

import com.chen.pojo.entity.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModelVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 人物模型id
     */
    private Integer modelId;
    /**
     * hr的名称
     */
    private String name;
    private String image;
    /**
     * 用于ai风格 0~2.0 值越大越有创意
     */
    private Double temperature;
    private String openMessage;
    private String remark;
    private CompanyVO company;

    
}
