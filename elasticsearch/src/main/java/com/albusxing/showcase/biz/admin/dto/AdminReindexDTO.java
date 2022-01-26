package com.albusxing.showcase.biz.admin.dto;

import lombok.Data;

/**
 * 重建索引请求体
 *
 * @author liguoqing*/
@Data
public class AdminReindexDTO {

    /**
     * 索引别名
     */
    private String aliasName;

    /**
     * 旧索引
     */
    private String oldIndex;

    /**
     * 新索引
     */
    private String newIndex;

    /**
     * 新索引mapping资源文件名
     * 须将文件上传至服务器的指定目录（配置文件中 elasticsearch.resourcePath配置）下
     */
    private String resourceName;

}
