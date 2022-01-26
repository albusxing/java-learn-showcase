package com.albusxing.showcase.biz.admin.controller;


import com.albusxing.showcase.biz.admin.dto.AdminGoodsStoreDTO;
import com.albusxing.showcase.biz.admin.entity.AdminGoodsStore;
import com.albusxing.showcase.biz.admin.service.AdminGoodsStoreService;
import com.albusxing.showcase.biz.common.base.CommonResponse;
import com.albusxing.showcase.biz.common.base.TableData;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author liguoqing
 */
@Api(tags = "后台店铺管理")
@Slf4j
@RestController
@RequestMapping("/admin/goods/store")
public class AdminGoodsStoreController {


    @Resource
    private AdminGoodsStoreService adminGoodsStoreService;

    @ApiOperation(value = "店铺分页查询")
    @PostMapping("/list")
    public CommonResponse<TableData<AdminGoodsStore>> list(@RequestBody AdminGoodsStoreDTO adminGoodsStoreDTO) {
        CommonResponse<TableData<AdminGoodsStore>> response;
        // 从 mysql 中查询
//        response = adminGoodsStoreService.getStorePageByStoreNameFromDB(adminGoodsStoreDTO);
        // 从 ES中查询
        response = adminGoodsStoreService.getStorePageByStoreNameFromEs(adminGoodsStoreDTO);
        return response;
    }

    @ApiOperation(value = "店铺分页查询DB")
    @PostMapping("/listByDB")
    public CommonResponse<TableData<AdminGoodsStore>> listByDB(@RequestBody AdminGoodsStoreDTO adminGoodsStoreDTO) {
        return adminGoodsStoreService.getStorePageByStoreNameFromDB(adminGoodsStoreDTO);
    }

    @ApiOperation(value = "店铺分页查询DB")
    @PostMapping("/listByES")
    public CommonResponse<TableData<AdminGoodsStore>> listByES(@RequestBody AdminGoodsStoreDTO adminGoodsStoreDTO) {
        return adminGoodsStoreService.getStorePageByStoreNameFromEs(adminGoodsStoreDTO);
    }

    @ApiOperation("添加店铺")
    @PostMapping("insert")
    public CommonResponse insertGoodsStore(@RequestBody AdminGoodsStore adminGoodsStore) {
        log.info("添加店铺，{}", JSON.toJSONString(adminGoodsStore));
        return adminGoodsStoreService.insertGoodsStore(adminGoodsStore);
    }


    @ApiOperation("根据id删除店铺信息")
    @GetMapping("/delete/{id}")
    public CommonResponse deleteStoreById(@PathVariable String id) {
        return adminGoodsStoreService.deleteStoreById(id);
    }


}
