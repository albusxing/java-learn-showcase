package com.albusxing.showcase.biz.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 后台店铺查询dto
 * @author liguoqing
 */
@Data
public class AdminGoodsStoreDTO extends PageRequest {

    @ApiModelProperty("商品名称")
    private String storeName;
}
