package com.albusxing.showcase.biz.admin.entity;

import com.albusxing.showcase.biz.common.dto.GoodsRelationField;
import lombok.Data;

import java.util.List;

/**
 * 商品spu,包含商品sku信息
 * @author liguoqing
 */
@Data
public class AdminGoodsSpuDetail {

    /**
     * 主键id
     */
    private String id;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 商品编号
     */
    private String goodsSpuNo;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品状态 {@link GoodsStatusEnum}
     */
    private String goodsStatus;

    /**
     * 商品上架时间
     */
    private String onlineTime;

    /**
     * 商品标签
     */
    private List<String> goodsTags;

    /**
     * 商品图片
     */
    private List<String> goodsPictures;


    /**
     * 关联关系字段
     */
    private GoodsRelationField goodsRelationField;

    /**
     * 商品spu下的商品sku列表
     */
    private List<AdminGoodsSku> adminGoodsSkuList;

    public AdminGoodsSpuDetail() {

    }

    public AdminGoodsSpuDetail(String parentId) {
        goodsRelationField = GoodsRelationField.getGoodsSpuRelationField(parentId);
    }

    /**
     * 关联店铺id
     * @param parentId 店铺id
     */
    public void setParentId(String parentId) {
        goodsRelationField = GoodsRelationField.getGoodsSpuRelationField(parentId);
    }
}
