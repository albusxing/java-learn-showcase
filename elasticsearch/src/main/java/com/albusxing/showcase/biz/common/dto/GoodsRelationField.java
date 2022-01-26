package com.albusxing.showcase.biz.common.dto;

import lombok.Data;

/**
 * 商品关联关系
 * @author liguoqing
 */
@Data
public class GoodsRelationField {

    /**
     * 商品关联关系字段
     */
    public static final String RELATION_FIELD_NAME = "goodsRelationField";
    /**
     * 店铺
     */
    public static final String GOODS_STORE = "goodsStore";
    /**
     * Standard Product Unit 标准产品单元
     */
    public static final String GOODS_SPU = "goodsSpu";
    /**
     * Stock Keeping Unit 最小库存单位
     */
    public static final String GOODS_SKU = "goodsSku";

    /**
     * 商品sku最大数量，代码没有做限制，只是在查询的时候，指定大小。
     */
    public static final int GOODS_SKU_MAX_SIZE     = 20;

    /**
     * 关联关系中的类型名称
     */
    private String name;

    /**
     * 关联关系中的父类型id
     */
    private String parent;

    public GoodsRelationField() {
    }

    public GoodsRelationField(String name, String parent) {
        this.name = name;
        this.parent = parent;
    }

    /**
     * 获取商品spu的关联关系对象
     * @param parent 父类型id，即店铺id
     */
    public static GoodsRelationField getGoodsSpuRelationField(String parent) {
        return new GoodsRelationField(GOODS_SPU, parent);
    }

    /**
     * 获取商品sku的关联关系对象
     * @param parent 父类型id，即商品spuId
     * @return 结果
     */
    public static GoodsRelationField getGoodsSkuRelationField(String parent) {
        return new GoodsRelationField(GOODS_SKU, parent);
    }
}
