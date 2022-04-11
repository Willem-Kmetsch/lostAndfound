package com.design.common.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class AddLostDto {
    private Integer userId;
    private String name;
    private Integer type;
    private String place;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDateTime date1;
    @DateTimeFormat(pattern="HH:mm:ss")
    private LocalDateTime date2;
    private String desc;
    private Integer score;
    private String picurl;
}
