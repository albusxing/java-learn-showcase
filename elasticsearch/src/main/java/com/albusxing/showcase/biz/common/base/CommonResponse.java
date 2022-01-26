package com.albusxing.showcase.biz.common.base;

import java.io.Serializable;

/**
 * @author liguoqing
 */
public class CommonResponse<T> implements Serializable {


    /**
     * 响应编码 {@link ErrorCodeEnum}
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;


    public CommonResponse() {
        this.code = ErrorCodeEnum.SUCCESS.getCode();
        this.message = ErrorCodeEnum.SUCCESS.getMsg();
    }

    public CommonResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public CommonResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  数据泛型
     * @return 响应结果
     */
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<T>(ErrorCodeEnum.SUCCESS.getCode(), ErrorCodeEnum.SUCCESS.getMsg(), data);
    }

    /**
     * 成功
     *
     * @return 响应结果
     */
    public static CommonResponse success() {
        return new CommonResponse(ErrorCodeEnum.SUCCESS.getCode(), ErrorCodeEnum.SUCCESS.getMsg());
    }

    /**
     * 失败
     *
     * @return 响应结果
     */
    public static <T> CommonResponse<T> fail() {
        return new CommonResponse(ErrorCodeEnum.FAIL.getCode(), ErrorCodeEnum.FAIL.getMsg());
    }

    /**
     * 失败
     *
     * @param errorCodeEnum 错误编码
     * @return 响应结果
     */
    public static <T> CommonResponse<T> fail(ErrorCodeEnum errorCodeEnum) {
        return new CommonResponse(errorCodeEnum.getCode(), errorCodeEnum.getMsg());
    }

    /**
     * 失败
     *
     * @param errorCodeEnum 错误编码
     * @param exception     异常信息
     * @return 响应结果
     */
    public static <T> CommonResponse<T> fail(ErrorCodeEnum errorCodeEnum, String exception) {
        return new CommonResponse(errorCodeEnum.getCode(), errorCodeEnum.getMsg() + exception);
    }
}
