package com.chen.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferDTO {

    //    1.表示接收了offer 0.拒绝
    @NotNull
    private Integer type;
    private String sessionId;


}
