package com.albusxing.showcase.biz.api.entity;

import lombok.Data;

/**
 * 商品sku
 **/
@Data
public class GoodsSku {

    /**
     * 主键id
     */
    private String id;

    /**
     * 商品颜色
     */
    private String goodsColor;

    /**
     * 商品存储容量
     */
    private String goodsMemoryCapacity;

    /**
     * 商品价格
     */
    private Double goodsPrice;

    /**
     * 商品销售数量
     */
    private Integer goodsSaleNum = 0;

    /**
     * 商品剩余数量
     */
    private Integer goodsRemainNum = 0;

}
