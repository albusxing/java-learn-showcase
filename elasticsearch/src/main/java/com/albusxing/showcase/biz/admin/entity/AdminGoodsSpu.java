package com.albusxing.showcase.biz.admin.entity;
import com.albusxing.showcase.biz.common.dto.GoodsRelationField;
import lombok.Data;

import java.util.List;

/**
 * 商品spu
 * 不包含商品sku信息
 * @author liguoqing*/
@Data
public class AdminGoodsSpu {

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
     * 商品标签
     */
    private List<String> goodsTags;

    /**
     * 商品图片
     */
    private List<String> goodsPictures;

    /**
     * 商品状态
     * {@link GoodsStatusEnum}
     */
    private String goodsStatus;

    /**
     * 商品定时上架时间
     */
    private String onlineTime;

    /**
     * 关联关系字段
     */
    private GoodsRelationField goodsRelationField;


    public AdminGoodsSpu() {

    }

    public AdminGoodsSpu(String parentId) {
        goodsRelationField = GoodsRelationField.getGoodsSpuRelationField(parentId);
    }

    /**
     * 关联店铺id
     */
    public void setParentId(String parentId) {
        goodsRelationField = GoodsRelationField.getGoodsSpuRelationField(parentId);
    }
}
