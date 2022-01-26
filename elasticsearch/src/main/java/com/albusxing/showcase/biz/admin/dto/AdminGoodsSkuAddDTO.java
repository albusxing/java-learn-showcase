package com.albusxing.showcase.biz.admin.dto;


import com.albusxing.showcase.biz.admin.entity.AdminGoodsSku;
import com.albusxing.showcase.biz.admin.entity.AdminGoodsSpu;
import lombok.Data;

import java.util.List;

/**
 * 商品SKU添加对象
 *
 * @author liguoqing
 * */
@Data
public class AdminGoodsSkuAddDTO {

    /**
     * 店铺id
     */
    private String storeId;

    /**
     * 商品spu
     */
    private AdminGoodsSpu adminGoodsSpu;

    /**
     * 商品sku
     */
    private List<AdminGoodsSku> adminGoodsSkuList;
}
