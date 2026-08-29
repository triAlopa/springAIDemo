package com.chen.pojo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferDTO {

    //    1.表示接收了offer 0.拒绝
    @NotNull(message = "offer操作类型不能为空")
    private Integer type;
    @NotBlank(message = "会话id不能为空")
    private String sessionId;


}
