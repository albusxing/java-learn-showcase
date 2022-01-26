package com.albusxing.showcase.biz.api.controller;
import com.albusxing.showcase.biz.api.dto.GoodsSearchDTO;
import com.albusxing.showcase.biz.api.service.GoodsService;
import com.albusxing.showcase.biz.common.base.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author liguoqing
 */
@Api(tags = "商品搜索")
@Slf4j
@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @ApiOperation("根据搜索字段获取自动补全结果")
    @PostMapping("/spu/searchSuggest")
    public CommonResponse searchSpuSuggest(@RequestParam(value = "context") String context) {
        // 使用 Wildcard
        // return goodsService.searchSpuSuggestWithWildcard(context);
        // 使用 Suggest
        return goodsService.searchSpuSuggest(context);
    }


    @ApiOperation("分页查询商品列表")
    @PostMapping("/spu/list")
    public CommonResponse getSpuList(GoodsSearchDTO goodsSearchDTO) {
        // 指定了排序字段，查询排序的商品列表
        if (StringUtils.hasLength(goodsSearchDTO.getOrderField())) {
            return goodsService.getOrderSpuList(goodsSearchDTO);
        }
        return goodsService.getSpuList(goodsSearchDTO);
    }

    @ApiOperation("根据店铺id获取店铺信息")
    @RequestMapping("/store/{id}")
    public CommonResponse getStoreById(@PathVariable String id,
                                       @RequestParam(value = "page") Integer page,
                                       @RequestParam(value = "size") Integer size) {
        return goodsService.getStoreById(id, page, size);
    }

    /**
     *
     *
     * @param id id
     * @return 结果
     */
    @ApiOperation("根据商品spuId获取商品spu详情")
    @GetMapping("/spu/detail/{id}")
    public CommonResponse getSpuDetailById(@PathVariable String id) {
        return goodsService.getSpuDetailById(id);
    }

}
