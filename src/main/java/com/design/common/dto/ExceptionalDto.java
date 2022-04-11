package com.design.common.dto;

import com.design.entity.Found;
import lombok.Data;

@Data
public class ExceptionalDto {
    private Integer userId;

    private Integer founderId;

    private Integer foundId;

    private Integer value;

}
