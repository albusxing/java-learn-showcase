package com.albusxing.showcase.biz.admin.service.impl;


import com.albusxing.showcase.biz.admin.dto.AdminGoodsStoreDTO;
import com.albusxing.showcase.biz.common.util.ElasticClientUtil;
import com.albusxing.showcase.biz.admin.entity.AdminGoodsStore;
import com.albusxing.showcase.biz.admin.mapper.AdminGoodsStoreMapper;
import com.albusxing.showcase.biz.admin.service.AdminGoodsStoreService;
import com.albusxing.showcase.biz.common.base.CommonResponse;
import com.albusxing.showcase.biz.common.base.TableData;
import com.albusxing.showcase.biz.common.constant.ElasticsearchProperties;
import com.albusxing.showcase.biz.common.constant.QueryFiledNameConstant;
import com.albusxing.showcase.biz.common.constant.StringConstant;
import com.albusxing.showcase.biz.common.dao.ElasticsearchDao;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.UUIDs;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author liguoqing
 */
@Service
@Slf4j
public class AdminGoodsStoreServiceImpl implements AdminGoodsStoreService {

    @Resource
    private AdminGoodsStoreMapper adminGoodsStoreMapper;
    @Resource
    private ElasticsearchProperties elasticsearchProperties;
    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Resource
    private ElasticsearchDao elasticsearchDao;

    @Override
    public CommonResponse<TableData<AdminGoodsStore>> getStorePageByStoreNameFromDB(AdminGoodsStoreDTO adminGoodsStoreDTO) {
        log.info("?????????????????????????????????????????????:{}", JSON.toJSONString(adminGoodsStoreDTO));
        Integer page = adminGoodsStoreDTO.getPage();
        // page???????????????
        if (Objects.isNull(page) || page == 0) {
            page = 1;
        }

        TableData<AdminGoodsStore> tableData = new TableData<>();

        String storeName = adminGoodsStoreDTO.getStoreName();
        // ??????????????????
        long count = adminGoodsStoreMapper.countStorePageByStoreName(storeName);
        tableData.setTotal(count);
        if (count > 0) {
            // ????????????
            Integer size = adminGoodsStoreDTO.getSize();
            List<AdminGoodsStore> goodsStoreList = adminGoodsStoreMapper.getStorePageByStoreName(storeName, (page - 1) * size, size);
            tableData.setRows(goodsStoreList);
        }
        log.info("?????????????????????????????????????????????:{}", JSON.toJSONString(tableData));
        return CommonResponse.success(tableData);
    }

    @Override
    public CommonResponse<TableData<AdminGoodsStore>> getStorePageByStoreNameFromEs(AdminGoodsStoreDTO adminGoodsStoreDTO) {
        log.info("????????????????????????ES??????????????????, ??????:{}", JSON.toJSONString(adminGoodsStoreDTO));

        String storeName = adminGoodsStoreDTO.getStoreName();
        Integer page = adminGoodsStoreDTO.getPage();
        Integer size = adminGoodsStoreDTO.getSize();

        // ?????????????????????????????????
        SearchSourceBuilder searchSourceBuilder = this.buildStorePageQueryBuilder(storeName, page, size);

        // ????????????
        String storeIndex = elasticsearchProperties.getStoreIndexAlias();
        SearchRequest searchRequest = new SearchRequest(storeIndex);
        searchRequest.source(searchSourceBuilder);

        try {
            log.info("???es???????????????????????????={}", searchRequest.source().toString());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            log.info("???es???????????????????????????={}", JSON.toJSONString(searchResponse));

            TableData<AdminGoodsStore> tableData =  this.buildStorePageResult(searchResponse);
            log.info("????????????????????????ES???????????????????????????:{}", JSON.toJSONString(tableData));
            return CommonResponse.success(tableData);

        } catch (IOException e) {
            log.error("???es????????????????????????", e);
        }
        return CommonResponse.fail();
    }

    /**
     * ??????es???????????????????????????
     */
    private SearchSourceBuilder buildStorePageQueryBuilder(String storeName, Integer page, Integer size) {

        SearchSourceBuilder searchSourceBuilder = ElasticClientUtil.buildPageSearchBuilder(page, size);

        if (!ObjectUtils.isEmpty(storeName)) {
            // storeName.keyword
            String name = QueryFiledNameConstant.STORE_NAME + StringConstant.DOT + QueryFiledNameConstant.KEYWORD;
            // *storeName*
            String query = StringConstant.STAR + storeName + StringConstant.STAR;
            // ?????????????????????????????????????????????
            searchSourceBuilder.query(
                    QueryBuilders.wildcardQuery(name, query)
            );
        }
        return searchSourceBuilder;
    }

    /**
     * ?????????????????????????????????
     */
    private TableData<AdminGoodsStore> buildStorePageResult(SearchResponse searchResponse) {
        TableData<AdminGoodsStore> tableData = new TableData<>();

        SearchHits hits = searchResponse.getHits();
        long total = hits.getTotalHits().value;
        tableData.setTotal(total);

        List<AdminGoodsStore> adminGoodsStores = new ArrayList<>();
        if (total > 0) {
            for (SearchHit hit : hits) {
                AdminGoodsStore adminGoodsStore = JSON.parseObject(hit.getSourceAsString(), AdminGoodsStore.class);
                adminGoodsStores.add(adminGoodsStore);
            }
        }
        tableData.setRows(adminGoodsStores);
        return tableData;
    }

    @Override
    public CommonResponse insertGoodsStore(AdminGoodsStore adminGoodsStore) {
        String storeIndexAlias = elasticsearchProperties.getStoreIndexAlias();
        adminGoodsStore.setId(UUIDs.base64UUID());
        IndexRequest indexRequest = new IndexRequest(storeIndexAlias);
        indexRequest.id(adminGoodsStore.getId());
        adminGoodsStore.setOpenDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        indexRequest.source(JSONObject.toJSONString(adminGoodsStore), XContentType.JSON);
        return elasticsearchDao.insert(indexRequest);
    }

    @Override
    public CommonResponse deleteStoreById(String id) {
        String storeIndexAlias = elasticsearchProperties.getStoreIndexAlias();
        DeleteRequest deleteRequest = new DeleteRequest(storeIndexAlias, id);
        return elasticsearchDao.delete(deleteRequest);
    }
}
