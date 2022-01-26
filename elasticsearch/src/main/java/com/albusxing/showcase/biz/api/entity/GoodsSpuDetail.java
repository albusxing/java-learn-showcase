package com.albusxing.showcase.biz.api.entity;

import lombok.Data;

import java.util.List;

/**
 * 商品spu详情，包含商品sku信息
 **/
@Data
public class GoodsSpuDetail {

    /**
     * 主键id
     */
    private String id;

    /**
     * 商品编号
     */
    private String goodsSpuNo;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品图片
     */
    private List<String> goodsPictures;

    /**
     * 店铺id
     */
    private String storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 商品sku列表
     */
    private List<GoodsSku> goodsSkuList;
}
