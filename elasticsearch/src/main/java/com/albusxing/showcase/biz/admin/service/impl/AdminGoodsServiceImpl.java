package com.albusxing.showcase.biz.admin.service.impl;
import com.albusxing.showcase.biz.admin.dto.AdminGoodsSpuDTO;
import com.albusxing.showcase.biz.admin.entity.AdminGoodsSku;
import com.albusxing.showcase.biz.admin.entity.AdminGoodsSpu;
import com.albusxing.showcase.biz.admin.entity.AdminGoodsSpuDetail;
import com.albusxing.showcase.biz.admin.entity.AdminGoodsStore;
import com.albusxing.showcase.biz.admin.service.AdminGoodsService;
import com.albusxing.showcase.biz.common.base.CommonResponse;
import com.albusxing.showcase.biz.common.base.TableData;
import com.albusxing.showcase.biz.common.constant.ElasticsearchProperties;
import com.albusxing.showcase.biz.common.constant.QueryFiledNameConstant;
import com.albusxing.showcase.biz.common.constant.StringConstant;
import com.albusxing.showcase.biz.common.dao.ElasticsearchDao;
import com.albusxing.showcase.biz.common.dto.GoodsRelationField;
import com.albusxing.showcase.biz.common.enums.GoodsStatusEnum;
import com.albusxing.showcase.biz.common.util.ElasticClientUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.UUIDs;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.join.query.HasParentQueryBuilder;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liguoqing
 */
@Service
@Slf4j
public class AdminGoodsServiceImpl implements AdminGoodsService {


    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Resource
    private ElasticsearchProperties elasticsearchProperties;
    @Resource
    private ElasticsearchDao elasticsearchDao;

    /**
     * 第一个商品spuId，根据商品初始化文档固定一个数据
     */
    private String firstGoodsSpuId = "4";


    @Override
    public CommonResponse init() {
        // 查询商品索引是否存在
        String goodsIndex = elasticsearchProperties.getGoodsIndex();
        try {
            boolean indexExistFlag = elasticsearchDao.existIndex(goodsIndex);
            if (!indexExistFlag) {
                // 不存在
                // 创建商品索引
                String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
                org.springframework.core.io.Resource goodsIndexMappingResource = elasticsearchProperties.getGoodsIndexMappingResource();
                Boolean created = elasticsearchDao.createIndex(goodsIndex, goodsIndexAlias, goodsIndexMappingResource);
                log.info("create index {} ", created ? "success" : "fail");
            } else {
                log.info("index:{} already exist", goodsIndex);
            }
            // 查询商品信息是否存在，根据初始化数据来指定对应的id
            boolean documentExistFlag = elasticsearchDao.existDoc(goodsIndex,firstGoodsSpuId);
            if (!documentExistFlag) {
                // 不存在
                // 初始化商品信息
                this.initGoodsInfo();
                log.info("initialization goods info finished");
            } else {
                log.info("goods info already exist");
            }
            return CommonResponse.success();
        } catch (Exception e) {
            log.error("初始化索引出现异常", e);
        }
        return CommonResponse.fail();
    }

    /**
     * 初始化商品信息
     * @throws IOException
     */
    private void initGoodsInfo() throws IOException {
        org.springframework.core.io.Resource goodsDocResource = elasticsearchProperties.getGoodsDocResource();
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();

        String docJson = IOUtils.toString(goodsDocResource.getInputStream());
        JSONObject docJsonObject = JSON.parseObject(docJson);

        // 使用 bulk api批量执行写入
        BulkRequest bulkRequest = new BulkRequest();

        // 店铺信息
        JSONArray storeJsonArray = docJsonObject.getJSONArray(GoodsRelationField.GOODS_STORE);
        log.info("初始化店铺信息：{}",storeJsonArray.toJSONString());
        for (int i = 0; i < storeJsonArray.size(); i++) {
            JSONObject store = storeJsonArray.getJSONObject(i);
            IndexRequest indexRequest = new IndexRequest(goodsIndexAlias);
            indexRequest.id(store.getString(StringConstant.ID));
            indexRequest.source(store.toJSONString(), XContentType.JSON);
//            elasticsearchDao.insert(indexRequest);
            bulkRequest.add(indexRequest);
        }

        // 商品spu信息
        JSONArray goodsSpuJsonArray = docJsonObject.getJSONArray(GoodsRelationField.GOODS_SPU);
        if (CollectionUtils.isEmpty(goodsSpuJsonArray)){
            return;
        }
        log.info("初始化商品spu信息：{}",goodsSpuJsonArray.toJSONString());
        for (int i = 0; i < goodsSpuJsonArray.size(); i++) {
            JSONObject goodsSpu = goodsSpuJsonArray.getJSONObject(i);
            IndexRequest indexRequest = new IndexRequest(goodsIndexAlias);
            indexRequest.id(goodsSpu.getString(StringConstant.ID));
            indexRequest.routing(goodsSpu.getString(StringConstant.ROUTING));
            // 移除多余字段
            goodsSpu.remove(StringConstant.ROUTING);
            indexRequest.source(goodsSpu.toJSONString(), XContentType.JSON);
//            elasticsearchDao.insert(indexRequest);
            bulkRequest.add(indexRequest);
        }

        // 商品sku信息
        JSONArray goodsSkuJsonArray = docJsonObject.getJSONArray(GoodsRelationField.GOODS_SKU);
        log.info("初始化商品sku信息：{}",goodsSkuJsonArray.toJSONString());
        for (int i = 0; i < goodsSkuJsonArray.size(); i++) {
            JSONObject goodsSku = goodsSkuJsonArray.getJSONObject(i);
            IndexRequest indexRequest = new IndexRequest(goodsIndexAlias);
            indexRequest.id(goodsSku.getString(StringConstant.ID));
            indexRequest.routing(goodsSku.getString(StringConstant.ROUTING));
            // 移除多余字段
            goodsSku.remove(StringConstant.ROUTING);
            indexRequest.source(goodsSku.toJSONString(), XContentType.JSON);
//            elasticsearchDao.insert(indexRequest);
            bulkRequest.add(indexRequest);
        }
        // 批量初始化sku信息
        elasticsearchDao.bulk(bulkRequest);
    }

    @Override
    public CommonResponse list(AdminGoodsSpuDTO adminGoodsSpuDTO) {
        log.info("开始调用查询商品列表方法，参数:{}", JSON.toJSONString(adminGoodsSpuDTO));
        // 请求参数
        String goodsStoreId = adminGoodsSpuDTO.getStoreId();
        Integer page = adminGoodsSpuDTO.getPage();
        Integer size = adminGoodsSpuDTO.getSize();

        // 构建商品分页查询请求体
        SearchSourceBuilder sourceBuilder = this.buildGoodsPageQueryBuilder(goodsStoreId, adminGoodsSpuDTO.getGoodsName(), page, size);

        // 查询请求
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        SearchRequest searchRequest = new SearchRequest(goodsIndexAlias);
        searchRequest.source(sourceBuilder);

        try {
            log.info("从es查询商品请求参数：{}", searchRequest.source().toString());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 构建查询结果
            TableData<AdminGoodsSpu> tableData = this.buildGoodsPageResult(searchResponse);
            log.info("结束调用查询商品列表方法，结果:{}", JSON.toJSONString(tableData));
            return CommonResponse.success(tableData);
        } catch (IOException e) {
            log.error("从es查询店铺列表失败", e);
        }
        return CommonResponse.fail();
    }

    /**
     * 构建分页查询商品信息的请求体
     */
    private SearchSourceBuilder buildGoodsPageQueryBuilder(String goodsStoreId, String goodsName, Integer page, Integer size) {
        // 获取分页查询构造器
        SearchSourceBuilder sourceBuilder = ElasticClientUtil.buildPageSearchBuilder(page, size);

        // 指定父文档的筛选条件
        QueryBuilder parentQueryBuilder;
        // 1、先对店铺进行查询
        // 商品店铺id不为空，根据id筛选，否则查询所有
        if (StringUtils.hasLength(goodsStoreId)) {
            parentQueryBuilder = new TermQueryBuilder(QueryFiledNameConstant.UNDERSCORE_ID, goodsStoreId);
        }else {
            parentQueryBuilder = new MatchAllQueryBuilder();
        }

        // 指定hasParent查询条件，限定查询结果的父文档类型是 店铺：goodsStore
        HasParentQueryBuilder hasParentQueryBuilder =
                new HasParentQueryBuilder(GoodsRelationField.GOODS_STORE, parentQueryBuilder, false)
                        .innerHit(new InnerHitBuilder());
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(hasParentQueryBuilder);


        // 2、再对商品名称进行查询
        // 商品名称是否为空
        if (StringUtils.hasLength(goodsName)) {
            // wildcardQuery：正则表达式匹配查询
            boolQueryBuilder.must(QueryBuilders.wildcardQuery(QueryFiledNameConstant.GOODS_NAME,
                    StringConstant.STAR + goodsName + StringConstant.STAR));
        }

        sourceBuilder.query(boolQueryBuilder);
        return sourceBuilder;
    }


    /**
     * 构建商品分页查询结果
     */
    private TableData<AdminGoodsSpu> buildGoodsPageResult(SearchResponse searchResponse) {
        TableData<AdminGoodsSpu> tableData = new TableData<>();
        SearchHits hits = searchResponse.getHits();
        long total = hits.getTotalHits().value;
        tableData.setTotal(total);

        // 店铺列表
        List<AdminGoodsSpu> adminGoodsSpuList = new ArrayList<>();
        if (total > 0) {
            // 封装数据
            for (SearchHit hit : hits) {
                AdminGoodsSpu adminGoodsSpu = JSON.parseObject(hit.getSourceAsString(), AdminGoodsSpu.class);
                adminGoodsSpuList.add(adminGoodsSpu);
            }
        }
        tableData.setRows(adminGoodsSpuList);
        return tableData;
    }


    @Override
    public CommonResponse getSpuDetailById(String id) {
        log.info("开始调用查询商品spu详情方法，id:{}", id);
        // 构建根据id查询商品spu详情的查询资源构建器
        SearchSourceBuilder sourceBuilder = this.buildSpuDetailByIdSource(id);

        // 查询请求
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        SearchRequest searchRequest = new SearchRequest(goodsIndexAlias);
        searchRequest.source(sourceBuilder);
        try {
            log.info("ES请求参数：{}", searchRequest.source().toString());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 构建查询结果
            AdminGoodsSpuDetail adminGoodsSpuDetail = this.buildSpuDetailByIdResult(id, searchResponse);
            log.info("开始调用查询商品spu详情方法，结果:{}", JSON.toJSONString(adminGoodsSpuDetail));

            return CommonResponse.success(adminGoodsSpuDetail);
        } catch (IOException e) {
            log.error("查询商品明细失败", e);
        }
        return CommonResponse.fail();

    }

    /**
     * 构建根据id查询商品spu详情的查询资源构建器
     * @param id 商品spuId
     */
    private SearchSourceBuilder buildSpuDetailByIdSource(String id) {
        // 根据id查询商品spu
        IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery().addIds(id);

        // 查询商品spu的父类型，店铺信息
        HasParentQueryBuilder hasParentQueryBuilder =
                // 1、查询所有的店铺
                new HasParentQueryBuilder(GoodsRelationField.GOODS_STORE, QueryBuilders.matchAllQuery(), false)
                        .innerHit(new InnerHitBuilder());

        // 查询商品spu的子类型，商品sku信息，InnerHitBuilder默认size是3，因为商品spu下面的商品sku很可能不止3，所以设置一下size。
        HasChildQueryBuilder hasChildQueryBuilder =
                new HasChildQueryBuilder(GoodsRelationField.GOODS_SKU, QueryBuilders.matchAllQuery(), ScoreMode.None)
                        .innerHit(new InnerHitBuilder().setSize(GoodsRelationField.GOODS_SKU_MAX_SIZE));

        // bool查询构建器，整合查询条件
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // 根据 id 查询商品spu信息，要代码 店铺信息和商品sku信息
        boolQueryBuilder.must(idsQueryBuilder).must(hasParentQueryBuilder).must(hasChildQueryBuilder);

        // 查询资源构建器
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQueryBuilder);
        return sourceBuilder;
    }

    /**
     * 构建根据id查询商品spu的查询结果
     * @param id 商品id
     */
    private AdminGoodsSpuDetail buildSpuDetailByIdResult(String id, SearchResponse searchResponse) {
        // 商品spu信息
        SearchHits hits = searchResponse.getHits();
        if (hits.getTotalHits().value <= 0) {
            // 商品不存在
            throw new RuntimeException("商品id:" + id + "不存在");
        }

        // 商品存在 封装响应信息
        SearchHit searchHit = hits.getHits()[0];
        // 商品详情
        AdminGoodsSpuDetail adminGoodsSpuDetail = JSON.parseObject(searchHit.getSourceAsString(), AdminGoodsSpuDetail.class);

        // 封装店铺名称
        this.buildStoreName(searchHit, adminGoodsSpuDetail);

        // 商品sku列表信息
        this.buildSkuList(searchHit, adminGoodsSpuDetail);

        return adminGoodsSpuDetail;
    }

    /**
     * 封装店铺名称
     * @param searchHit             查询结果
     * @param adminGoodsSpuDetail   商品spu详细信息
     */
    private void buildStoreName(SearchHit searchHit, AdminGoodsSpuDetail adminGoodsSpuDetail) {
        // hasParent或者hasChild查询结果
        Map<String, SearchHits> innerHits = searchHit.getInnerHits();
        // 店铺信息
        SearchHits storeSearchHits = innerHits.get(GoodsRelationField.GOODS_STORE);
        AdminGoodsStore adminGoodsStore = JSON.parseObject(storeSearchHits.getHits()[0].getSourceAsString(), AdminGoodsStore.class);
        adminGoodsSpuDetail.setStoreName(adminGoodsStore.getStoreName());
    }

    /**
     * 构建商品sku列表信息
     * @param searchHit             查询结果
     * @param adminGoodsSpuDetail   商品spu详细信息
     */
    private void buildSkuList(SearchHit searchHit, AdminGoodsSpuDetail adminGoodsSpuDetail) {
        // hasParent或者hasChild查询结果
        Map<String, SearchHits> innerHits = searchHit.getInnerHits();
        // 查询商品sku
        SearchHits skuSearchHits = innerHits.get(GoodsRelationField.GOODS_SKU);

        // 封装商品sku数据
        List<AdminGoodsSku> adminGoodsSkuList = new ArrayList<>();
        for (SearchHit hit : skuSearchHits) {
            AdminGoodsSku adminGoodsSku = JSON.parseObject(hit.getSourceAsString(), AdminGoodsSku.class);
            adminGoodsSkuList.add(adminGoodsSku);
        }
        adminGoodsSpuDetail.setAdminGoodsSkuList(adminGoodsSkuList);
    }


    @Override
    public CommonResponse insertGoodsSpu(AdminGoodsSpu adminGoodsSpu, String parentId) {
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        adminGoodsSpu.setId(UUIDs.base64UUID());
        IndexRequest indexRequest = new IndexRequest(goodsIndexAlias);
        indexRequest.id(adminGoodsSpu.getId());
        adminGoodsSpu.setGoodsSpuNo(UUIDs.base64UUID());
        adminGoodsSpu.setParentId(parentId);
        // 指定路由
        indexRequest.routing(parentId);
        indexRequest.source(JSONObject.toJSONString(adminGoodsSpu), XContentType.JSON);
        return elasticsearchDao.insert(indexRequest);
    }

    @Override
    public CommonResponse insertGoodsSku(AdminGoodsSpu adminGoodsSpu, List<AdminGoodsSku> adminGoodsSkuList) {
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        // SPU为空
        if (ObjectUtils.isEmpty(adminGoodsSpu.getId())) {
            CommonResponse response = insertGoodsSpu(adminGoodsSpu, adminGoodsSpu.getGoodsRelationField().getParent());
            adminGoodsSpu.setId(String.valueOf(response.getData()));
        }
        // 单条商品sku循环insert到es中
//        adminGoodsSkuList.forEach(adminGoodsSku -> {
//            adminGoodsSku.setParentId(adminGoodsSpu.getId());
//            adminGoodsSku.setId(UUIDs.base64UUID());
//            IndexRequest indexRequest = new IndexRequest(goodsIndexAlias);
//            indexRequest.id(adminGoodsSku.getId());
//            indexRequest.routing(adminGoodsSpu.getGoodsRelationField().getParent());
//            indexRequest.source(JSONObject.toJSONString(adminGoodsSku), XContentType.JSON);
//            elasticsearchDao.insert(indexRequest);
//        });
//        return CommonResponse.success();

        // 使用bulk一次性insert多条商品sku到es中
        BulkRequest bulkRequest = new BulkRequest();
        adminGoodsSkuList.forEach(adminGoodsSku -> {
            adminGoodsSku.setParentId(adminGoodsSpu.getId());
            adminGoodsSku.setId(UUIDs.base64UUID());
            IndexRequest indexRequest = new IndexRequest(goodsIndexAlias);
            indexRequest.id(adminGoodsSku.getId());
            indexRequest.routing(adminGoodsSpu.getGoodsRelationField().getParent());
            indexRequest.source(JSONObject.toJSONString(adminGoodsSku), XContentType.JSON);
            bulkRequest.add(indexRequest);
        });
        return elasticsearchDao.bulk(bulkRequest);
    }

    @Override
    public CommonResponse updateGoodsSpu(AdminGoodsSpu adminGoodsSpu) {
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        UpdateRequest updateRequest = new UpdateRequest(goodsIndexAlias, adminGoodsSpu.getId());
        updateRequest.doc(JSONObject.toJSONString(adminGoodsSpu), XContentType.JSON);
        return elasticsearchDao.update(updateRequest);
    }

    @Override
    public CommonResponse updateGoodsSku(AdminGoodsSku adminGoodsSku) {
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        UpdateRequest updateRequest = new UpdateRequest(goodsIndexAlias, adminGoodsSku.getId());
        updateRequest.doc(JSONObject.toJSONString(adminGoodsSku), XContentType.JSON);
        return elasticsearchDao.update(updateRequest);
    }

    @Override
    public CommonResponse deleteGoodsById(String id) {
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        DeleteRequest deleteRequest = new DeleteRequest(goodsIndexAlias, id);
        return elasticsearchDao.delete(deleteRequest);
    }

    @Override
    public void autoOnlineGoodsSpuByOnlineTime(String onlineTime) {
        log.info("开始调用自动上架方法，onlineTime={}", onlineTime);
        // 构建查询指定时间之前未上架的商品信息的请求体
        SearchSourceBuilder sourceBuilder = this.buildOnlineTimeGoodsSpuSource(onlineTime);

        // 查询请求
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        SearchRequest searchRequest = new SearchRequest(goodsIndexAlias);
        searchRequest.source(sourceBuilder);

        try {
            log.info("ES请求参数：{}", searchRequest.source().toString());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 构建查询结果
            List<AdminGoodsSpu> adminGoodsSpuList = this.buildAutoOnlineSearchResult(searchResponse);
            // 自动上架商品
            this.autoOnlineGoodsSpu(adminGoodsSpuList);

            log.info("结束调用自动上架方法");
        } catch (IOException e) {
            log.error("自动上架指定时间之前的商品", e);
        }
    }

    /**
     * 构建查询指定时间之前未上架的商品信息的请求体
     * @param dateTime 指定时间
     */
    private SearchSourceBuilder buildOnlineTimeGoodsSpuSource(String dateTime) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 构建查询所有未上架的上架时间在指定时间之前的商品
        TermQueryBuilder statusQuery =
                // 查询商品状态为未上架的
                QueryBuilders.termQuery(QueryFiledNameConstant.GOODS_STATUS, GoodsStatusEnum.OFFLINE.getCode());
        // 上架时间已到的
        RangeQueryBuilder timeQuery = QueryBuilders.rangeQuery(QueryFiledNameConstant.ONLINE_TIME).lte(dateTime);
        sourceBuilder.query(new BoolQueryBuilder().filter(timeQuery).filter(statusQuery));
        return sourceBuilder;
    }

    /**
     * 构建自动上架商品查询结果集
     */
    private List<AdminGoodsSpu> buildAutoOnlineSearchResult(SearchResponse searchResponse) {
        List<AdminGoodsSpu> adminGoodsSpuList = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            AdminGoodsSpu adminGoodsSpu = JSON.parseObject(hit.getSourceAsString(), AdminGoodsSpu.class);
            adminGoodsSpuList.add(adminGoodsSpu);
        }
        return adminGoodsSpuList;
    }

    /**
     * 自动上架商品
     */
    private void autoOnlineGoodsSpu(List<AdminGoodsSpu> adminGoodsSpuList) {
        // 单条商品数据循环update
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        // 循环修改状态为在线
//        adminGoodsSpuList.forEach(adminGoodsSpu -> {
//            adminGoodsSpu.setGoodsStatus(GoodsStatusEnum.ONLINE.getCode());
//            UpdateRequest updateRequest = new UpdateRequest(goodsIndexAlias, adminGoodsSpu.getId());
//            updateRequest.doc(JSONObject.toJSONString(adminGoodsSpu), XContentType.JSON);
//            elasticsearchDao.update(updateRequest);
//        });

        // 使用bulk多条商品数据一次性update
        BulkRequest bulkRequest = new BulkRequest();
        adminGoodsSpuList.forEach(adminGoodsSpu -> {
            adminGoodsSpu.setGoodsStatus(GoodsStatusEnum.ONLINE.getCode());
            UpdateRequest updateRequest = new UpdateRequest(goodsIndexAlias, adminGoodsSpu.getId());
            updateRequest.doc(JSONObject.toJSONString(adminGoodsSpu), XContentType.JSON);
            bulkRequest.add(updateRequest);
        });
        elasticsearchDao.bulk(bulkRequest);
    }


    @Override
    public void autoOnlineGoodsSpuByOnlineTimeScroll(String onlineTime) {
        log.info("开始调用自动上架方法，onlineTime={}", onlineTime);
        // 构建查询指定时间之前未上架的商品信息的请求体
        SearchSourceBuilder sourceBuilder = this.buildOnlineTimeGoodsSpuSource(onlineTime);

        // 查询请求
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        Long scrollExpireSeconds = elasticsearchProperties.getScrollExpireSeconds();
        Integer autoOnlinePageSize = elasticsearchProperties.getAutoOnlinePageSize();

        SearchRequest searchRequest = new SearchRequest(goodsIndexAlias);
        // 指定scroll失效时间为60秒，
        Scroll scroll = new Scroll(TimeValue.timeValueSeconds(scrollExpireSeconds));
        searchRequest.scroll(scroll);
        // 设定每次查询数据量大小
        sourceBuilder.size(autoOnlinePageSize);
        searchRequest.source(sourceBuilder);

        try {
            log.info("ES请求参数：{}", searchRequest.source().toString());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // scroll查询的id
            String scrollId = searchResponse.getScrollId();

            // 构建查询结果
            List<AdminGoodsSpu> adminGoodsSpuList = this.buildAutoOnlineSearchResult(searchResponse);

            while (!CollectionUtils.isEmpty(adminGoodsSpuList)) {
                // 自动上架商品
                autoOnlineGoodsSpu(adminGoodsSpuList);

                // 重新构建scroll查询
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                // 发起查询
                searchResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
                // scroll查询的id
                scrollId = searchResponse.getScrollId();
                // 构建查询结果
                adminGoodsSpuList = this.buildAutoOnlineSearchResult(searchResponse);
            }
            log.info("结束调用自动上架方法");
        } catch (IOException e) {
            log.error("自动上架指定时间之前的商品", e);
        }
    }

    @Override
    public CommonResponse reindex(String indexAlias, String oldIndex, String newIndex, String resourceName) {
        try {
            String resourcePath = elasticsearchProperties.getResourcePath();
            org.springframework.core.io.Resource mappingResource = new PathResource(resourcePath + resourceName);
            // 创建新的商品索引 newIndex
            Boolean created = elasticsearchDao.createIndex(newIndex, indexAlias, mappingResource);
            if (created){
                // 将oldIndex的数据，写入newIndex中
                elasticsearchDao.reindex(oldIndex, newIndex);

                // 将索引别名指向的索引从oldIndex修改为newIndex
                Boolean changed = elasticsearchDao.changeAliasAfterReindex(indexAlias,oldIndex, newIndex);
                if (changed){
                    return CommonResponse.success();
                }
            }
        } catch (IOException e) {
            log.error("reindex fail, oldIndex :{} newIndex :{} alias :{}", oldIndex, newIndex, indexAlias, e);
        }
        return CommonResponse.fail();
    }
}
