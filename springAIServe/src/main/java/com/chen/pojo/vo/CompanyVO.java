package com.chen.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyVO {

    private String companyId;
    /**
     * 公司的名称
     */
    private String name;
    /**
     * 公司类型 1.初创公司 2.上市公司 3.500强公司
     */
    private Integer type;
    /**
     * 最低薪水单位 k
     */
    private Integer lowSalary;
    /**
     * 最低薪水单位 k
     */
    private Integer highSalary;
    /**
     * 公司位置 一般是存储经纬度
     */
    private String address;
    /**
     * 职位详情标签 用空格隔开
     */
    private List<String> jobTag;
    /**
     * 职位具体要求
     */
    private String jobDesc;
    private Integer enable;
    /**
     * 员工福利 用空格隔开
     */
    private List<String> employerBenefit;

    private List<ModelVO> models;

}
