package com.chen.pojo.entity;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
* 公司表
* @TableName tb_ai_company
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company implements Serializable {

    /**
    * 主键自增
    */
    private Integer id;
    /**
    * 公司id
    */
    @Size(max= 20,message="编码长度不能超过20")
    private String companyId;
    /**
    * 公司的名称
    */
    @Size(max= 20,message="编码长度不能超过20")
    @Length(max= 20,message="编码长度不能超过20")
    private String name;
    /**
    * 最低薪水单位 k
    */
    @NotNull(message="[最低薪水单位 k]不能为空")
    private Integer lowSalary;
    /**
    * 最低薪水单位 k
    */
    @NotNull(message="[最低薪水单位 k]不能为空")
    private Integer highSalary;
    /**
    * 公司位置 一般是存储经纬度
    */
    @Size(max= 50,message="编码长度不能超过50")
    private String address;
    /**
    * 职位详情标签 用空格隔开
    */
    @Size(max= 255,message="编码长度不能超过255")
    private String jobTag;
    /**
    * 职位具体要求
    */
    @Size(max= 255,message="编码长度不能超过255")
    private String jobDesc;
    /**
    * 员工福利 用空格隔开
    */
    @Size(max= 255,message="编码长度不能超过255")
    private String employerBenefit;
    /**
    * 模型的状态，0.表示禁用 1.开启
    */
    private Integer enable;
    /**
    * 逻辑删除 0.表示删除 1.正常
    */
    private Integer isDel;
    /**
    * 创建会话时间
    */
    private Date createdTime;
    /**
    * 修改时间
    */
    private Date updateTime;
    /**
    * 修改的用户id
    */
    private Integer updateUserId;

}
