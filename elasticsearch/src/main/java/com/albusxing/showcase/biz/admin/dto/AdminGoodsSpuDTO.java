package com.albusxing.showcase.biz.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 后台商品查询DTO
 * @author liguoqing*/
@Data
public class AdminGoodsSpuDTO extends PageRequest {

    @ApiModelProperty(value = "店铺id")
    private String storeId;

    @ApiModelProperty(value = "商品名称")
    private String  goodsName;


}
