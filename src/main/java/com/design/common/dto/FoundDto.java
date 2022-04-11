package com.design.common.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class FoundDto implements Serializable {
    private String school;
    private Integer type;
    private String place;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date beginDate;


}
