package com.albusxing.showcase.biz.common.enums;

import lombok.Getter;

@Getter
public enum QueryTypeEnum {

    /**
     * 查询采用模糊查询
     */
    FUZZY(1,"模糊查询"),

    /**
     * 查询不采用模糊查询
     */
    NOT_FUZZY(2,"不模糊查询")
    ;

    Integer code;
    String message;

    QueryTypeEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

}
