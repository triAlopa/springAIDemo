package com.chen.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String nickName;
    private Integer gender;
    private Date birthday;
    private String password;
    private String email;
    private Integer points;
    private Integer enable;
    private Integer isDel;

    @JsonFormat(pattern = "yyyy-dd-MM HH:mm:ss")
    private LocalDateTime registerTime;
    private Date cancelTime;
}
