package com.albusxing.showcase.biz.api.entity;

import lombok.Data;

import java.util.List;

/**
 * 店铺实体
 **/
@Data
public class GoodsStore {

    /**
     * 主键id
     */
    private String id;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺简介
     */
    private String storeIntroduction;

    /**
     * 店铺品牌
     */
    private String storeBrand;

    /**
     * 店铺开店时间
     */
    private String openDate;

    /**
     * 店铺图片
     */
    private List<String> storePhoto;

    /**
     * 店铺标签
     */
    private List<String> storeTags;
}
