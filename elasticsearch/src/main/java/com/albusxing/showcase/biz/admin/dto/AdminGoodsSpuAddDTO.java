package com.albusxing.showcase.biz.admin.dto;

import com.albusxing.showcase.biz.admin.entity.AdminGoodsSpu;
import lombok.Data;

/**
 * 商品SPU添加对象
 * @author liguoqing*/
@Data
public class AdminGoodsSpuAddDTO {
    /**
     * 店铺id
     */
    private String storeId;

    /**
     * 商品spu信息
     */
    private AdminGoodsSpu adminGoodsSpu;
}
