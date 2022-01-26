package com.albusxing.showcase.biz.api.dto;

import com.albusxing.showcase.biz.admin.dto.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品搜索对象
 * @author liguoqing
 */
@Data
public class GoodsSearchDTO extends PageRequest {

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("起始金额范围")
    private Double startPrice;

    @ApiModelProperty("结束金额范围")
    private Double endPrice;

    @ApiModelProperty("排序字段")
    private String orderField;

    @ApiModelProperty("排序类型 asc desc")
    private String orderType;

    /**
     * {@link QueryTypeEnum}
     */
    @ApiModelProperty("查询类型")
    private Integer queryType;

}
