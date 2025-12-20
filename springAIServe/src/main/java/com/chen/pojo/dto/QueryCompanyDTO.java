package com.chen.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static com.chen.constant.UserConstant.NODEL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryCompanyDTO {

    private String name;
    private Integer type;
    private Integer pageSize;
    private Integer pageNum;

}
