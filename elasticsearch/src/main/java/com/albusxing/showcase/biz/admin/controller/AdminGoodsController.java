package com.albusxing.showcase.biz.admin.controller;
import com.albusxing.showcase.biz.admin.dto.AdminGoodsSkuAddDTO;
import com.albusxing.showcase.biz.admin.dto.AdminGoodsSpuAddDTO;
import com.albusxing.showcase.biz.admin.dto.AdminGoodsSpuDTO;
import com.albusxing.showcase.biz.admin.dto.AdminReindexDTO;
import com.albusxing.showcase.biz.admin.entity.AdminGoodsSku;
import com.albusxing.showcase.biz.admin.entity.AdminGoodsSpu;
import com.albusxing.showcase.biz.admin.service.AdminGoodsService;
import com.albusxing.showcase.biz.common.constant.StringConstant;
import com.albusxing.showcase.biz.common.base.CommonResponse;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author liguoqing
 */
@Api(tags = "后台商品管理")
@Slf4j
@RestController
@RequestMapping("/admin/goods")
public class AdminGoodsController {

    @Resource
    private AdminGoodsService adminGoodsService;

    @ApiOperation("查询商品spu列表")
    @PostMapping("/list")
    public CommonResponse list(@RequestBody AdminGoodsSpuDTO adminGoodsSpuDTO) {
        // 查询elasticsearch
        return adminGoodsService.list(adminGoodsSpuDTO);
    }


    @ApiOperation("查询商品spu详情")
    @GetMapping("/{id}")
    public CommonResponse getSpuDetailById(@PathVariable("id") String id) {
        // 查询elasticsearch
        return adminGoodsService.getSpuDetailById(id);
    }

    @ApiOperation("单独添加商品spu")
    @PostMapping("/spu/insert")
    @Deprecated
    public CommonResponse insertGoodsSpu(@RequestBody AdminGoodsSpuAddDTO adminGoodsSpuAddDTO) {
        log.info("添加商品spu，{}", JSON.toJSONString(adminGoodsSpuAddDTO));
        return adminGoodsService.insertGoodsSpu(adminGoodsSpuAddDTO.getAdminGoodsSpu(), adminGoodsSpuAddDTO.getStoreId());
    }

    @ApiOperation("添加商品spu和sku")
    @PostMapping("/sku/insert")
    public CommonResponse insertGoodsSku(@RequestBody AdminGoodsSkuAddDTO adminGoodsSkuAddDTO) {
        log.info("添加商品spu，{}", JSON.toJSONString(adminGoodsSkuAddDTO));
        AdminGoodsSpu adminGoodsSpu = adminGoodsSkuAddDTO.getAdminGoodsSpu();
        // 如果上架时间为空，以当前时间为上架时间
        if (!StringUtils.hasLength(adminGoodsSpu.getOnlineTime())){
            adminGoodsSpu.setOnlineTime(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern(StringConstant.FULL_DATE_TIME)));
        }
        adminGoodsSpu.setParentId(adminGoodsSkuAddDTO.getStoreId());
        return adminGoodsService.insertGoodsSku(adminGoodsSpu, adminGoodsSkuAddDTO.getAdminGoodsSkuList());
    }

    @ApiOperation("修改商品spu")
    @PostMapping("/spu/update")
    public CommonResponse updateGoodsSpu(@RequestBody AdminGoodsSpu adminGoodsSpu) {
        log.info("修改商品spu，{}", JSON.toJSONString(adminGoodsSpu));
        return adminGoodsService.updateGoodsSpu(adminGoodsSpu);
    }

    @ApiOperation("修改商品sku")
    @PostMapping("/sku/update")
    public CommonResponse updateGoodsSku(@RequestBody AdminGoodsSku adminGoodsSku) {
        log.info("修改商品sku，{}", JSON.toJSONString(adminGoodsSku));
        return adminGoodsService.updateGoodsSku(adminGoodsSku);
    }

    @ApiOperation("根据id删除商品信息")
    @GetMapping("/delete/{id}")
    public CommonResponse deleteGoodsById(@PathVariable String id) {
        return adminGoodsService.deleteGoodsById(id);
    }


    @ApiOperation("初始化商品索引")
    @GetMapping("/initGoodsIndex")
    public CommonResponse initGoodsIndex() {
        return adminGoodsService.init();
    }

    @ApiOperation("重建索引")
    @PostMapping("/reindex")
    public CommonResponse reindex(@RequestBody AdminReindexDTO adminReindexDTO){
        return adminGoodsService.reindex(adminReindexDTO.getAliasName(), adminReindexDTO.getOldIndex(),
                adminReindexDTO.getNewIndex(), adminReindexDTO.getResourceName());
    }

}
