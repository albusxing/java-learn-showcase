package com.albusxing.showcase.biz.common.enums;

/**
 * 商品上架状态
 **/
public enum GoodsStatusEnum {

    /**
     * 商品上架状态
     */
    ONLINE("1","上架"),

    /**
     * 商品未下架状态
     */
    OFFLINE("2","未上架"),
    ;

    private String code;
    private String message;

    GoodsStatusEnum(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
