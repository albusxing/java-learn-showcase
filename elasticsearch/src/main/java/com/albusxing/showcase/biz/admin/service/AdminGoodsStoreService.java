package com.albusxing.showcase.biz.admin.service;


import com.albusxing.showcase.biz.admin.dto.AdminGoodsStoreDTO;
import com.albusxing.showcase.biz.admin.entity.AdminGoodsStore;
import com.albusxing.showcase.biz.common.base.CommonResponse;
import com.albusxing.showcase.biz.common.base.TableData;

/**
 * @author liguoqing
 */
public interface AdminGoodsStoreService {

    /**
     * 根据店铺名称获取店铺列表 从MySQL查
     */
    CommonResponse<TableData<AdminGoodsStore>> getStorePageByStoreNameFromDB(AdminGoodsStoreDTO adminGoodsStoreDTO);


    /**
     * 根据店铺名称获取店铺列表 从es查询
     */
    CommonResponse<TableData<AdminGoodsStore>> getStorePageByStoreNameFromEs(AdminGoodsStoreDTO adminGoodsStoreDTO);


    /**
     * 添加店铺
     */
    CommonResponse insertGoodsStore(AdminGoodsStore adminGoodsStore);

    /**
     * 根据id删除商品
     */
    CommonResponse deleteStoreById(String id);
}
