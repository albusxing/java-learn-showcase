package com.albusxing.showcase.biz.common.base;

import java.util.Objects;

/**
 * 错误码枚举
 * @author liguoqing
 */
public enum ErrorCodeEnum {

    /**
     * 200 - 成功
     * 500 - 失败
     */
    SUCCESS(200, "成功"),

    FAIL(500, "系统异常"),
    ;

    private Integer code;

    private String msg;

    ErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static ErrorCodeEnum getByMessage(String message) {
        ErrorCodeEnum[] errorCodeEnums = ErrorCodeEnum.values();
        for (ErrorCodeEnum errorCodeEnum : errorCodeEnums) {
            if (Objects.equals(errorCodeEnum.getMsg(), message)) {
                return errorCodeEnum;
            }
        }
        return null;
    }
}
