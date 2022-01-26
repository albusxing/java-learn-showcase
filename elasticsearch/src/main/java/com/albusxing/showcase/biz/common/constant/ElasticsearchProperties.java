package com.albusxing.showcase.biz.common.constant;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Data
@Component
//@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchProperties {

    /**
     * 店铺索引的mapping
     */
    @Value(value = "classpath:/mapping/storeIndex.json")
    private Resource storeIndexMappingResource;
    /**
     * 店铺index别名
     */
    @Value("${elasticsearch.storeIndexAlias}")
    private String storeIndexAlias;
    /**
     * 店铺index名称
     */
    @Value("${elasticsearch.storeIndex}")
    private String storeIndex;

    /**
     * 商品index别名
     */
    @Value("${elasticsearch.goodsIndexAlias}")
    private String goodsIndexAlias;

    /**
     * 商品index名称
     */
    @Value("${elasticsearch.goodsIndex}")
    private String goodsIndex;

    /**
     * 商品的mapping
     */
    @Value(value = "classpath:/mapping/goodsIndexV1.json")
    private Resource goodsIndexMappingResource;
    /**
     * 商品文档json
     */
    @Value(value = "classpath:/mapping/goodsDocV1.json")
    private Resource goodsDocResource;

    /**
     * 订单index别名
     */
    @Value("${elasticsearch.orderIndexAlias}")
    private String orderIndexAlias;

    /**
     * 订单index别名
     */
    @Value("${elasticsearch.orderIndex}")
    private String orderIndex;

    /**
     * 订单的mapping
     */
    @Value(value = "classpath:/mapping/orderIndex.json")
    private Resource orderIndexMappingResource;

    /**
     * scroll查询失效时间，单位：秒
     */
    @Value("${elasticsearch.scrollExpireSeconds}")
    private Long scrollExpireSeconds;

    /**
     * 自动上架分页数据大小
     */
    @Value("${elasticsearch.autoOnlinePageSize}")
    private Integer autoOnlinePageSize;

    /**
     * mapping资源路径
     */
    @Value("${elasticsearch.resourcePath}")
    private String resourcePath;

    /**
     * 不使用fuzzy查询的最小值，查询结果的记录数不小于该值就不使用fuzzy查询
     */
    @Value("${elasticsearch.notUsedFuzzyMinCount}")
    private Integer notUsedFuzzyMinCount;

}
