package com.design.common.lang;

import lombok.Data;

@Data
public class PageData {
    private Integer page;
    private Object data;

    public PageData(Integer page, Object data){
        this.page = page;
        this.data = data;
    }
}
