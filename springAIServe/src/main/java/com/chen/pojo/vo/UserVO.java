package com.chen.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String nickName;
    private Integer gender;
    private String email;
    private String image;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private Integer points;
    private Integer enable;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime registerTime;

}
