package com.albusxing.showcase.biz.admin.entity;
import com.albusxing.showcase.biz.common.dto.GoodsRelationField;
import lombok.Data;

/**
 * 商品sku
 * @author liguoqing*/
@Data
public class AdminGoodsSku {

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
     * 商品剩余数量
     */
    private Integer goodsRemainNum = 0;


    /**
     * 关联关系字段
     */
    private GoodsRelationField goodsRelationField;


    public AdminGoodsSku() {

    }

    public AdminGoodsSku(String parentId) {
        goodsRelationField = GoodsRelationField.getGoodsSkuRelationField(parentId);
    }

    /**
     * 关联商品spuId
     * @param parentId 商品spuId
     */
    public void setParentId(String parentId) {
        goodsRelationField = GoodsRelationField.getGoodsSkuRelationField(parentId);
    }
}
