package com.albusxing.showcase.biz.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liguoqing
 */
@Data
public class PageRequest {

    @ApiModelProperty(value = "页号")
    private Integer page = 1;
    @ApiModelProperty(value = "每页大小")
    private Integer size = 10;

}
