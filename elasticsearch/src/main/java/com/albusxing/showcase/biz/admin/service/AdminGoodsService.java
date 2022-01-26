package com.albusxing.showcase.biz.admin.service;


import com.albusxing.showcase.biz.admin.dto.AdminGoodsSpuDTO;
import com.albusxing.showcase.biz.admin.entity.AdminGoodsSku;
import com.albusxing.showcase.biz.admin.entity.AdminGoodsSpu;
import com.albusxing.showcase.biz.common.base.CommonResponse;

import java.util.List;

/**
 * @author liguoqing
 */
public interface AdminGoodsService {

    /**
     * 初始化商品索引
     * @return
     */
    CommonResponse init();

    /**
     * 查询商品列表
     */
    CommonResponse list(AdminGoodsSpuDTO adminGoodsSpuDTO);

    /**
     * 根据id查询商品spu信息
     */
    CommonResponse getSpuDetailById(String id);

    /**
     * 添加商品spu
     */
    CommonResponse insertGoodsSpu(AdminGoodsSpu adminGoodsSpu, String parentId);

    /**
     * 添加商品sku
     */
    CommonResponse insertGoodsSku(AdminGoodsSpu adminGoodsSpu, List<AdminGoodsSku> adminGoodsSkuList);

    /**
     * 修改商品spu
     */
    CommonResponse updateGoodsSpu(AdminGoodsSpu adminGoodsSpu);

    /**
     * 修改商品sku
     */
    CommonResponse updateGoodsSku(AdminGoodsSku adminGoodsSku);

    /**
     * 根据id删除商品
     */
    CommonResponse deleteGoodsById(String id);

    /**
     * 自动上架指定时间之前的商品
     * @param onlineTime 上架时间
     */
    void autoOnlineGoodsSpuByOnlineTime(String onlineTime);

    /**
     * 使用Scroll自动上架指定时间之前的商品
     * @param onlineTime 上架时间
     */
    void autoOnlineGoodsSpuByOnlineTimeScroll(String onlineTime);

    /**
     * 重建索引
     * @param indexAlias    索引别名
     * @param oldIndex      旧索引
     * @param newIndex      新索引
     * @param resourceName  新索引mapping文件名称
     * @return 结果
     */
    CommonResponse reindex(String indexAlias, String oldIndex, String newIndex, String resourceName);

}
