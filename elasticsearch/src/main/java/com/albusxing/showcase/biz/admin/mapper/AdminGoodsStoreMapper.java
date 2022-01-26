package com.albusxing.showcase.biz.admin.mapper;
import com.albusxing.showcase.biz.admin.entity.AdminGoodsStore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminGoodsStoreMapper {

    /**
     * 根据店铺名称分页查询店铺信息总数
     */
    Integer countStorePageByStoreName(@Param("storeName") String storeName);

    /**
     * 根据店铺名称分页查询店铺信息
     */
    List<AdminGoodsStore> getStorePageByStoreName(@Param("storeName") String storeName,
                                                  @Param("start") Integer start,
                                                  @Param("size") Integer size);
}
