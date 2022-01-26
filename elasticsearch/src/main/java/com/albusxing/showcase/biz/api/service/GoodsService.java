package com.albusxing.showcase.biz.api.service;

import com.albusxing.showcase.biz.api.dto.GoodsSearchDTO;
import com.albusxing.showcase.biz.common.base.CommonResponse;

/**
 * @author liguoqing
 */
public interface GoodsService {


    /**
     * 通过wildcard查询商品spu推荐建议
     * @param context 搜索内容
     */
    CommonResponse searchSpuSuggestWithWildcard(String context);

    /**
     * 使用Suggest查询商品spu推荐建议
     * @param context 搜索内容
     */
    CommonResponse searchSpuSuggest(String context);


    /**
     * 分页查询商品列表
     * @param goodsSearchDTO 商品查询请求信息
     */
    CommonResponse getSpuList(GoodsSearchDTO goodsSearchDTO);

    /**
     * 排序分页查询商品列表
     * @param goodsSearchDTO 商品查询请求信息
     */
    CommonResponse getOrderSpuList(GoodsSearchDTO goodsSearchDTO);

    /**
     * 根据id查询商品Spu详情
     * @param id 商品spuId
     * @return 结果
     */
    CommonResponse getSpuDetailById(String id);

    /**
     * 根据店铺id获取店铺信息
     * @param id   店铺id
     * @param page 页码
     * @param size 每页大小
     * @return 结果
     */
    CommonResponse getStoreById(String id, Integer page, Integer size);

}
