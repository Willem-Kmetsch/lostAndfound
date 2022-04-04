package com.design.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author god
 * @since 2022-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("found_finish")
public class FoundFinish implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private Integer type;

    private LocalDateTime foundtime;

    private String foundplace;

    private Integer founderid;

    private String picture;

    private String itemexplain;

    private Integer ownerid;

    private LocalDateTime finishtime;

    private Integer score;


}