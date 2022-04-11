package com.design.common.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class AddFoundDto {
    private Integer userId;
    private String name;
    private Integer type;
    private String place;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDateTime date1;
    @DateTimeFormat(pattern="HH:mm:ss")
    private LocalDateTime date2;
    private String desc;
    private String picurl;

}
