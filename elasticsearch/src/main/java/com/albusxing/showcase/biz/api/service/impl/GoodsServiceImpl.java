package com.albusxing.showcase.biz.api.service.impl;
import com.albusxing.showcase.biz.api.dto.FuzzyQueryData;
import com.albusxing.showcase.biz.api.dto.GoodsSearchDTO;
import com.albusxing.showcase.biz.api.service.GoodsService;
import com.albusxing.showcase.biz.common.constant.ElasticsearchProperties;
import com.albusxing.showcase.biz.common.constant.QueryFiledNameConstant;
import com.albusxing.showcase.biz.common.constant.StringConstant;
import com.albusxing.showcase.biz.common.enums.GoodsStatusEnum;
import com.albusxing.showcase.biz.common.enums.QueryTypeEnum;
import com.albusxing.showcase.biz.common.util.ElasticClientUtil;
import com.albusxing.showcase.biz.api.entity.*;
import com.albusxing.showcase.showcase.biz.api.entity.*;
import com.albusxing.showcase.biz.common.base.CommonResponse;
import com.albusxing.showcase.biz.common.base.TableData;
import com.albusxing.showcase.biz.common.dto.GoodsRelationField;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.join.aggregations.Children;
import org.elasticsearch.join.aggregations.JoinAggregationBuilders;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.join.query.HasParentQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private ElasticsearchProperties elasticsearchProperties;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public CommonResponse searchSpuSuggestWithWildcard(String context) {
        log.info("开始调用通过wildcard查询商品spu推荐建议方法，context={}", context);
        // 构建商品spu自动补全查询请求体
        SearchSourceBuilder sourceBuilder = this.buildSpuSuggestWithWildcardSource(context);

        // 查询请求
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        SearchRequest searchRequest = new SearchRequest(goodsIndexAlias);
        searchRequest.source(sourceBuilder);
        try {
            log.info("ES请求参数：{}", searchRequest.source().toString());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            Set<String> resultSet = this.buildSpuSuggestWithWildcardResult(searchResponse);
            log.info("结束调用通过wildcard查询商品spu推荐建议方法，result={}", JSON.toJSONString(resultSet));
            return CommonResponse.success(resultSet);
        } catch (IOException e) {
            log.error("调用通过wildcard查询商品spu推荐建议出错", e);
        }
        return CommonResponse.fail();
    }


    /**
     * 构建商品spu自动补全查询请求体
     */
    private SearchSourceBuilder buildSpuSuggestWithWildcardSource(String context) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 这里是获取商品的自动补全，只需要在结果中返回商品名称字段就好了
        sourceBuilder.fetchSource(QueryFiledNameConstant.GOODS_NAME, null);

        // 查询字段不为空，则添加店铺名称查询
        if (!ObjectUtils.isEmpty(context)) {
            sourceBuilder.query(QueryBuilders.wildcardQuery(
                    // goodsName.keyword
                    QueryFiledNameConstant.GOODS_NAME + StringConstant.DOT + QueryFiledNameConstant.KEYWORD,
                    // *.context.*
                    StringConstant.STAR + context + StringConstant.STAR));
        }

        return sourceBuilder;
    }

    /**
     * 构建使用wildcard实现商品自动补全结果集
     */
    private Set<String> buildSpuSuggestWithWildcardResult(SearchResponse searchResponse) {
        // 防止结果重复，采用set集合
        Set<String> resultSet = new LinkedHashSet<>();

        for (SearchHit hit : searchResponse.getHits()) {
            resultSet.add(hit.getSourceAsMap().get(QueryFiledNameConstant.GOODS_NAME).toString());
        }

        return resultSet;
    }

    @Override
    public CommonResponse searchSpuSuggest(String context) {
        log.info("开始调用通过Suggest查询商品spu推荐方法，context={}", context);
        // 构建商品spu自动补全查询资源构建器
        SearchSourceBuilder sourceBuilder = this.buildSpuSuggestSource(context);

        // 查询请求
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        SearchRequest searchRequest = new SearchRequest(goodsIndexAlias);
        searchRequest.source(sourceBuilder);
        try {
            log.info("ES请求参数：{}", searchRequest.source().toString());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 防止结果重复，采用set集合
            Set<String> resultSet = this.buildSpuSuggestWithSuggestResult(searchResponse);
            log.info("结束调用通过Suggest查询商品spu推荐方法，result={}", JSON.toJSONString(resultSet));
            return CommonResponse.success(resultSet);
        } catch (IOException e) {
            log.error("查询商品列表失败", e);
        }
        return CommonResponse.fail();
    }

    /**
     * 构建商品spu自动补全查询资源构建器
     * @param context 搜索内容
     */
    private SearchSourceBuilder buildSpuSuggestSource(String context) {
        // 查询资源构建器
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 这里是获取商品的自动补全，只需要在结果中返回商品名称字段就好了
        sourceBuilder.fetchSource(QueryFiledNameConstant.GOODS_NAME, null);

        // 搜索建议
        SuggestionBuilder<CompletionSuggestionBuilder> suggestionBuilder =
                SuggestBuilders.completionSuggestion(
                        // goodsName.suggest
                        QueryFiledNameConstant.GOODS_NAME +
                                StringConstant.DOT + QueryFiledNameConstant.SUGGEST).prefix(context);

        // 搜索建议构建器
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion(QueryFiledNameConstant.GOODS_SUGGEST_NAME, suggestionBuilder);

        sourceBuilder.suggest(suggestBuilder);
        return sourceBuilder;
    }

    /**
     * 构建商品搜索自动补全结果集
     * @param searchResponse es搜索结果
     */
    private Set<String> buildSpuSuggestWithSuggestResult(SearchResponse searchResponse) {
        // FIXME 这里用LinkedHashSet有什么好处？
        Set<String> resultSet = new LinkedHashSet<>();
        // 获取搜索建议结果
        CompletionSuggestion suggestion = searchResponse.getSuggest().getSuggestion(QueryFiledNameConstant.GOODS_SUGGEST_NAME);
        List<CompletionSuggestion.Entry> entries = suggestion.getEntries();
        for (CompletionSuggestion.Entry entry : entries) {
            for (CompletionSuggestion.Entry.Option option : entry.getOptions()) {
                resultSet.add(option.getText().toString());
            }
        }
        return resultSet;
    }

    @Override
    public CommonResponse getSpuList(GoodsSearchDTO goodsSearchDTO) {
        log.info("开始调用分页查询商品列表，参数={}", JSON.toJSONString(goodsSearchDTO));
        // 构建查询商品spu请求体
        SearchSourceBuilder sourceBuilder = this.buildSpuListSource(goodsSearchDTO, false);

        // 查询请求
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        SearchRequest searchRequest = new SearchRequest(goodsIndexAlias);
        searchRequest.source(sourceBuilder);

        try {
            log.info("ES请求参数：{}", searchRequest.source().toString());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            boolean fuzzyQueryFlag = this.fuzzyQueryFlag(goodsSearchDTO, searchResponse);
            if (fuzzyQueryFlag) {
                log.info("指定了采用fuzzy查询，或者查询商品结果低于设定值，开始使用fuzzy查询商品列表");
                // 重新构建分页查询商品spu的查询资源构建器
                sourceBuilder = this.buildSpuListSource(goodsSearchDTO, true);
                searchRequest.source(sourceBuilder);

                log.info("ES fuzzy请求参数：{}", searchRequest.source().toString());
                searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            }

            TableData<GoodsSpu> tableData = this.buildSpuListResult(searchResponse, goodsSearchDTO, fuzzyQueryFlag);
            log.info("结束调用分页查询商品列表，结果={}", JSON.toJSONString(tableData));
            return CommonResponse.success(tableData);
        } catch (IOException e) {
            log.error("查询商品明细失败", e);
        }
        return CommonResponse.fail();
    }

    /**
     * 构建查询商品列表请求体
     * @param goodsSearchDTO 商品查询请求信息
     * @param fuzzyQueryFlag 是否采用模糊查询
     */
    private SearchSourceBuilder buildSpuListSource(GoodsSearchDTO goodsSearchDTO, boolean fuzzyQueryFlag) {
        // 以商品sku为查询起点
        // 查询资源构建器
        SearchSourceBuilder sourceBuilder =
                ElasticClientUtil.buildPageSearchBuilder(goodsSearchDTO.getPage(), goodsSearchDTO.getSize());

        // 查询商品spu的父类型，店铺信息
        QueryBuilder parentQueryBuild = QueryBuilders.matchAllQuery();
        HasParentQueryBuilder hasParentQueryBuilder =
                new HasParentQueryBuilder(GoodsRelationField.GOODS_STORE, parentQueryBuild, false)
                        .innerHit(new InnerHitBuilder());


        // 子类型查询没有筛选条件时
        // QueryBuilder childQueryBuilder = QueryBuilders.matchAllQuery();

        // 子类型的查询需要进行范围查询
        // 如果起始金额范围为空，则赋值为0，因为商品价格不能为负数
        if (Objects.nonNull(goodsSearchDTO.getStartPrice())) {
            goodsSearchDTO.setStartPrice(0d);
        }
        RangeQueryBuilder childQueryBuilder =
                QueryBuilders.rangeQuery(QueryFiledNameConstant.GOODS_PRICE).gte(goodsSearchDTO.getStartPrice());
        // 如果设定了结束金额范围，则增加查询范围控制
        if (Objects.nonNull(goodsSearchDTO.getEndPrice())) {
            childQueryBuilder.lte(goodsSearchDTO.getEndPrice());
        }

        // 查询子类型商品sku中的一条记录
        HasChildQueryBuilder hasChildQueryBuilder =
                new HasChildQueryBuilder(GoodsRelationField.GOODS_SKU, childQueryBuilder, ScoreMode.None)
                        .innerHit(new InnerHitBuilder().setSize(1));

        // 根据商品名称查询
        QueryBuilder goodsNameQueryBuilder;
        if (ObjectUtils.isEmpty(goodsSearchDTO.getGoodsName())) {
            goodsNameQueryBuilder = QueryBuilders.matchAllQuery();
        } else {
            if (fuzzyQueryFlag){
                goodsNameQueryBuilder = QueryBuilders.matchQuery(QueryFiledNameConstant.GOODS_NAME, goodsSearchDTO.getGoodsName())
                        .fuzziness(Fuzziness.AUTO);
            }else {
                goodsNameQueryBuilder = QueryBuilders.matchQuery(QueryFiledNameConstant.GOODS_NAME, goodsSearchDTO.getGoodsName());
            }
        }

        // 整合所有查询条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(hasParentQueryBuilder)
                .must(hasChildQueryBuilder)
                .must(goodsNameQueryBuilder)
                .must(QueryBuilders.termQuery(QueryFiledNameConstant.GOODS_STATUS, GoodsStatusEnum.ONLINE.getCode()));
        sourceBuilder.query(boolQuery);

        // 构建商品列表的聚合查询
        TermsAggregationBuilder aggregationBuilder = this.buildSpuListAggregation();
        sourceBuilder.aggregation(aggregationBuilder);

        // 高亮显示
        ElasticClientUtil.sourceBuilderAddHighlight(sourceBuilder, QueryFiledNameConstant.GOODS_NAME);
        return sourceBuilder;
    }


    /**
     * 构建查询商品列表结果集
     * @param searchResponse es查询响应
     */
    private TableData<GoodsSpu> buildSpuListResult(SearchResponse searchResponse,
                                                   GoodsSearchDTO goodsSearchDTO, Boolean fuzzyQueryFlag) {
        FuzzyQueryData<GoodsSpu> tableData = new FuzzyQueryData<>();

        // 构建商品列表聚合结果集
        Map<String, Integer> goodsSaleNumMap = this.buildSpuListAggregationResult(searchResponse);

        tableData.setTotal(searchResponse.getHits().getTotalHits().value);
        List<GoodsSpu> goodsSpuList = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            GoodsSpu goodsSpu = JSON.parseObject(hit.getSourceAsString(), GoodsSpu.class);

            // 获取高亮结果
            String highlightResult = ElasticClientUtil.buildHighlightResult(hit, QueryFiledNameConstant.GOODS_NAME,
                    goodsSearchDTO.getGoodsName());
            if (StringUtils.hasLength(highlightResult)) {
                goodsSpu.setGoodsName(highlightResult);
                // 获取模糊查询的字符串
                if (fuzzyQueryFlag){
                    String fuzzyContent = ElasticClientUtil.getHighlightContent(highlightResult);
                    if (StringUtils.hasLength(fuzzyContent)){
                        tableData.setQueryContent(goodsSearchDTO.getGoodsName());
                        tableData.setFuzzyContent(fuzzyContent);
                    }
                }
            }

            // 商品总销量
            Integer goodsSaleNum = goodsSaleNumMap.get(goodsSpu.getGoodsSpuNo());
            goodsSpu.setGoodsSaleNum(goodsSaleNum);


            // 获取父类信息，即店铺信息查询结果
            SearchHits parentSearchHits = hit.getInnerHits().get(GoodsRelationField.GOODS_STORE);
            // 因为父类信息只能有一个，所以直接取数组下标为0的数据
            GoodsStore goodsStore = JSON.parseObject(parentSearchHits.getHits()[0].getSourceAsString(), GoodsStore.class);
            goodsSpu.setStoreId(goodsStore.getId());
            goodsSpu.setStoreName(goodsStore.getStoreName());

            // 获取子类信息，即商品sku信息查询结果
            SearchHits childSearchHits = hit.getInnerHits().get(GoodsRelationField.GOODS_SKU);
            // 因为子类信息在查询的时候指定了只查一条，所以直接取数组下标为0的数据
            GoodsSku goodsSku = JSON.parseObject(childSearchHits.getHits()[0].getSourceAsString(), GoodsSku.class);
            goodsSpu.setShowGoodsSku(goodsSku);

            goodsSpuList.add(goodsSpu);
        }
        tableData.setRows(goodsSpuList);
        return tableData;
    }

    /**
     * 构建商品列表聚合查询请求体
     */
    private TermsAggregationBuilder buildSpuListAggregation() {
        // 聚合查询商品销量，首先以goodsSpuNo分组，然后添加子分组，指定子节点类型是goodsSku，并在子节点中统计商品销量
        TermsAggregationBuilder aggregationBuilder =
                // groupByGoodsSpuNo = goodsSpuNo
                AggregationBuilders.terms(QueryFiledNameConstant.GROUP_GOODS_SPU_NO).field(QueryFiledNameConstant.GOODS_SPU_NO)
                        .subAggregation(
                                // groupByGoodsSku = goodsSku
                                JoinAggregationBuilders.children(QueryFiledNameConstant.GROUP_GOODS_SKU, GoodsRelationField.GOODS_SKU)
                                        .subAggregation(
                                                // sumGoodsSaleNum = goodsSaleNum
                                                AggregationBuilders.sum(QueryFiledNameConstant.SUM_GOODS_SALE_NUM)
                                                        .field(QueryFiledNameConstant.GOODS_SALE_NUM)));
        return aggregationBuilder;
    }

    /**
     * 构建商品列表聚合查询请求体
     * @param searchResponse
     * @return
     */
    private Map<String, Integer> buildSpuListAggregationResult(SearchResponse searchResponse) {
        /*
        从searchResponse一步一步得到最终的销量。整行代码如下
        double value = ((Sum)
                ((Children)
                        ((Terms) searchResponse.getAggregations().get(QueryFiledNameConstant.GROUP_GOODS_SPU_NO))
                                .getBuckets().get(0).getAggregations().get(QueryFiledNameConstant.GROUP_GOODS_SKU))
                        .getAggregations().get(QueryFiledNameConstant.SUM_GOODS_SALE_NUM))
                .getValue();
        */

        // 定义商品销量Map，key为商品编号
        Map<String, Integer> goodsSaleNumMap = new HashMap<>(16);
        // 根据设置的聚合查询配置，解析聚合结果
        // 首先是 AggregationBuilders.terms(QueryFiledNameConstant.GROUP_GOODS_SPU_NO)，解析是就采用Terms对象来接收聚合结果
        Terms groupByGoodsSpuNo = searchResponse.getAggregations().get(QueryFiledNameConstant.GROUP_GOODS_SPU_NO);
        for (Terms.Bucket bucket : groupByGoodsSpuNo.getBuckets()) {
            // 然后是JoinAggregationBuilders.children(QueryFiledNameConstant.GROUP_GOODS_SKU, GoodsRelationField.GOODS_SKU)
            // 解析是就采用Children对象来接收聚合结果
            Children groupByGoodsSku = bucket.getAggregations().get(QueryFiledNameConstant.GROUP_GOODS_SKU);
            // 最后是AggregationBuilders.sum(QueryFiledNameConstant.SUM_GOODS_SALE_NUM)，解析是就采用Sum对象来接收聚合结果
            Sum sumGoodsSaleNum = groupByGoodsSku.getAggregations().get(QueryFiledNameConstant.SUM_GOODS_SALE_NUM);
            double sumGoodsSaleNumValue = sumGoodsSaleNum.getValue();
            if (Double.isInfinite(sumGoodsSaleNumValue)) {
                sumGoodsSaleNumValue = 0.0;
            }
            // 将结果存放在map中，bucket.getKeyAsString()为
            // AggregationBuilders.terms(QueryFiledNameConstant.GROUP_GOODS_SPU_NO).field(QueryFiledNameConstant.GOODS_SPU_NO)
            // 中指定的field QueryFiledNameConstant.GOODS_SPU_NO。
            goodsSaleNumMap.put(bucket.getKeyAsString(), (int) sumGoodsSaleNumValue);
        }
        return goodsSaleNumMap;
    }

    @Override
    public CommonResponse getOrderSpuList(GoodsSearchDTO goodsSearchDTO) {
        // 构建查询商品spu请求体
        SearchSourceBuilder sourceBuilder = this.buildOrderSpuListSource(goodsSearchDTO, false);
        // 查询请求
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        SearchRequest searchRequest = new SearchRequest(goodsIndexAlias);
        searchRequest.source(sourceBuilder);

        try {
            log.info("ES请求参数：{}", searchRequest.source().toString());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            boolean fuzzyQueryFlag = this.fuzzyQueryFlag(goodsSearchDTO, searchResponse);
            if (fuzzyQueryFlag) {
                log.info("指定了采用fuzzy查询，或者查询商品结果低于设定值，开始使用fuzzy查询商品列表");
                // 重新构建分页查询商品spu的查询资源构建器
                sourceBuilder = this.buildOrderSpuListSource(goodsSearchDTO, true);
                searchRequest.source(sourceBuilder);

                log.info("ES fuzzy请求参数：{}", searchRequest.source().toString());
                searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            }

            TableData<GoodsSpu> tableData = this.buildOrderSpuListResult(searchResponse);

            return CommonResponse.success(tableData);
        } catch (IOException e) {
            log.error("查询商品明细失败", e);
        }
        return CommonResponse.fail();
    }

    /**
     * 构建排序查询商品列表请求体
     * @param goodsSearchDTO 商品查询请求信息
     * @param fuzzyQueryFlag 是否使用fuzzy模糊查询
     */
    private SearchSourceBuilder buildOrderSpuListSource(GoodsSearchDTO goodsSearchDTO, boolean fuzzyQueryFlag) {
        // 以商品sku为查询起点

        // 查询资源构建器
        SearchSourceBuilder sourceBuilder =
                ElasticClientUtil.buildPageSearchBuilder(goodsSearchDTO.getPage(), goodsSearchDTO.getSize());

        // 查询商品sku的父类型，商品spu信息
        // 首先指定商品spu的父类型是店铺
        HasParentQueryBuilder storeQueryBuilder =
                new HasParentQueryBuilder(GoodsRelationField.GOODS_STORE, QueryBuilders.matchAllQuery(), false)
                        .innerHit(new InnerHitBuilder());

        // 根据商品名称查询
        QueryBuilder parentQueryBuild;
        if (ObjectUtils.isEmpty(goodsSearchDTO.getGoodsName())) {
            parentQueryBuild = QueryBuilders.matchAllQuery();
        } else {
            if (fuzzyQueryFlag){
                parentQueryBuild = QueryBuilders.matchQuery(QueryFiledNameConstant.GOODS_NAME, goodsSearchDTO.getGoodsName())
                        .fuzziness(Fuzziness.AUTO);
            }else {
                parentQueryBuild = QueryBuilders.matchQuery(QueryFiledNameConstant.GOODS_NAME, goodsSearchDTO.getGoodsName());
            }
        }

        BoolQueryBuilder parentBoolQueryBuilder =
                QueryBuilders.boolQuery().must(storeQueryBuilder).must(parentQueryBuild)
                        .must(QueryBuilders.termQuery(QueryFiledNameConstant.GOODS_STATUS, GoodsStatusEnum.ONLINE.getCode()));

        HasParentQueryBuilder hasParentQueryBuilder =
                new HasParentQueryBuilder(GoodsRelationField.GOODS_SPU, parentBoolQueryBuilder, false)
                        .innerHit(new InnerHitBuilder());

        // 子类型查询需要范围查询条件时
        // 如果起始金额范围为空，则赋值为0，因为商品价格不能为负数
        if (goodsSearchDTO.getStartPrice() == null) {
            goodsSearchDTO.setStartPrice(0.0);
        }

        // 构建范围查询
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(QueryFiledNameConstant.GOODS_PRICE).gte(goodsSearchDTO.getStartPrice());
        // 如果设定了结束金额范围，则增加查询范围控制
        if (goodsSearchDTO.getEndPrice() != null) {
            rangeQueryBuilder.lte(goodsSearchDTO.getEndPrice());
        }

        // 整合所有查询条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must(hasParentQueryBuilder).must(rangeQueryBuilder);
        sourceBuilder.query(boolQuery);

        // 添加排序规则
        sourceBuilder.sort(goodsSearchDTO.getOrderField(),
                SortOrder.valueOf(goodsSearchDTO.getOrderType().toUpperCase()));

        return sourceBuilder;
    }

    /**
     * 构建排序查询商品列表结果集
     */
    private TableData<GoodsSpu> buildOrderSpuListResult(SearchResponse searchResponse) {
        TableData<GoodsSpu> tableData = new TableData<>();
        tableData.setTotal(searchResponse.getHits().getTotalHits().value);
        List<GoodsSpu> goodsSpuList = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            // 商品sku信息
            GoodsSku goodsSku = JSON.parseObject(hit.getSourceAsString(), GoodsSku.class);

            // 获取父类信息，即spu信息查询结果 因为父类信息只能有一个，所以直接取数组下标为0的数据
            SearchHit spuSearchHit = hit.getInnerHits().get(GoodsRelationField.GOODS_SPU).getHits()[0];
            GoodsSpu goodsSpu = JSON.parseObject(spuSearchHit.getSourceAsString(), GoodsSpu.class);

            // 商品销量
            goodsSpu.setGoodsSaleNum(goodsSku.getGoodsSaleNum());

            // 获取spu信息的父类信息，即店铺信息查询结果
            SearchHit storeSearchHit = spuSearchHit.getInnerHits().get(GoodsRelationField.GOODS_STORE).getHits()[0];
            // 因为子类信息在查询的时候指定了只查一条，所以直接取数组下标为0的数据
            GoodsStore goodsStore = JSON.parseObject(storeSearchHit.getSourceAsString(), GoodsStore.class);

            goodsSpu.setStoreId(goodsStore.getId());
            goodsSpu.setStoreName(goodsStore.getStoreName());
            goodsSpu.setShowGoodsSku(goodsSku);

            goodsSpuList.add(goodsSpu);
        }
        tableData.setRows(goodsSpuList);
        return tableData;
    }

    /**
     * 判断是否需要模糊查询
     * @param goodsSearchDTO 商品查询请求体
     * @param searchResponse es查询响应
     * @return 结果
     */
    private Boolean fuzzyQueryFlag(GoodsSearchDTO goodsSearchDTO, SearchResponse searchResponse){
        // 如果查询字段为空，则不采用模糊查询
        if (!StringUtils.hasLength(goodsSearchDTO.getGoodsName())){
            return false;
        }
        // 指定使用模糊查询，则返回需要模糊查询
        if (QueryTypeEnum.FUZZY.getCode().equals(goodsSearchDTO.getQueryType())){
            return true;
        }
        long total = searchResponse.getHits().getTotalHits().value;
        Integer page = goodsSearchDTO.getPage();
        Integer notUsedFuzzyMinCount = elasticsearchProperties.getNotUsedFuzzyMinCount();
        // 未指定查询类型，查询的第一页的数据，且结果小于每页大小，且查询结果小于指定不使用模糊查询的最小值，则需要使用模糊查询
        if (goodsSearchDTO.getQueryType() == null && page != null && page == 1
                && total < goodsSearchDTO.getSize() && total < notUsedFuzzyMinCount){
            return true;
        }
        return false;
    }

    @Override
    public CommonResponse getSpuDetailById(String id) {
        // 构建根据id查询商品spu详情的查询资源构建器
        SearchSourceBuilder sourceBuilder = this.buildSpuDetailByIdSource(id);

        // 查询请求
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        SearchRequest searchRequest = new SearchRequest(goodsIndexAlias);
        searchRequest.source(sourceBuilder);
        try {
            log.info("ES请求参数：{}", searchRequest.source().toString());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            GoodsSpuDetail goodsSpuDetail = this.buildSpuDetailByIdResult(searchResponse);
            return CommonResponse.success(goodsSpuDetail);
        } catch (IOException e) {
            log.error("查询商品明细失败", e);
        }
        return CommonResponse.fail();
    }

    @Override
    public CommonResponse getStoreById(String id, Integer page, Integer size) {
        // 根据id查询店铺详情
        SearchSourceBuilder sourceBuilder = this.buildStoreByIdSource(id, page, size);

        // 查询请求
        String goodsIndexAlias = elasticsearchProperties.getGoodsIndexAlias();
        SearchRequest searchRequest = new SearchRequest(goodsIndexAlias);
        searchRequest.source(sourceBuilder);

        try {
            log.info("ES请求参数：{}", searchRequest.source().toString());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            GoodsStoreDetail goodsStoreDetail = this.buildStoreByIdResult(searchResponse);

            return CommonResponse.success(goodsStoreDetail);
        } catch (IOException e) {
            log.error("查询商品明细失败", e);
        }
        return CommonResponse.fail();
    }

    /**
     * 构建根据id查询店铺详情的请求体
     * @param id    店铺id
     * @param page  页码
     * @param size  每页大小
     * @return 结果
     */
    private SearchSourceBuilder buildStoreByIdSource(String id, Integer page, Integer size) {
        // 查询资源构建器
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 指定查询店铺id
        IdsQueryBuilder idsQueryBuilder = new IdsQueryBuilder().addIds(id);

        // 查询子类型商品sku中的一条记录
        HasChildQueryBuilder skuQueryBuilder =
                new HasChildQueryBuilder(GoodsRelationField.GOODS_SKU, QueryBuilders.matchAllQuery(), ScoreMode.None)
                        .innerHit(new InnerHitBuilder().setSize(1));

        // 查询子类型商品spu中的指定记录
        HasChildQueryBuilder spuQueryBuilder =
                new HasChildQueryBuilder(GoodsRelationField.GOODS_SPU, skuQueryBuilder, ScoreMode.None)
                        .innerHit(new InnerHitBuilder().setFrom((page - 1) * size).setSize(size));

        // 整合所有查询条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must(idsQueryBuilder).must(spuQueryBuilder);
        sourceBuilder.query(boolQuery);
        return sourceBuilder;
    }

    /**
     * 构建根据id查询店铺详情的结果集
     */
    private GoodsStoreDetail buildStoreByIdResult(SearchResponse searchResponse) {
        // 根据id查询只会有一条记录
        SearchHit hit = searchResponse.getHits().getHits()[0];
        GoodsStoreDetail goodsStoreDetail = JSON.parseObject(hit.getSourceAsString(), GoodsStoreDetail.class);

        // 店铺子类型商品spu查询结果
        SearchHits spuSearchHits = hit.getInnerHits().get(GoodsRelationField.GOODS_SPU);
        List<GoodsSpu> goodsSpuList = new ArrayList<>();
        for (SearchHit spuSearchHit : spuSearchHits) {
            GoodsSpu goodsSpu = JSON.parseObject(spuSearchHit.getSourceAsString(), GoodsSpu.class);
            // 商品spu子类型商品sku查询结果，因为指定了一条记录，所以直接取数组下标0的记录
            SearchHit skuSearchHit = spuSearchHit.getInnerHits().get(GoodsRelationField.GOODS_SKU).getHits()[0];
            GoodsSku goodsSku = JSON.parseObject(skuSearchHit.getSourceAsString(), GoodsSku.class);

            goodsSpu.setShowGoodsSku(goodsSku);
            goodsSpu.setStoreId(goodsStoreDetail.getId());
            goodsSpu.setStoreName(goodsStoreDetail.getStoreName());

            goodsSpuList.add(goodsSpu);
        }
        goodsStoreDetail.setGoodsSpuList(goodsSpuList);
        return goodsStoreDetail;
    }

    /**
     * 构建根据id查询商品spu详情的查询资源构建器
     *
     * @param id 商品spuId
     * @return 资源构建器
     */
    private SearchSourceBuilder buildSpuDetailByIdSource(String id) {
        // 根据id查询商品spu
        IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery().addIds(id);

        // 查询商品spu的父类型，店铺信息
        HasParentQueryBuilder hasParentQueryBuilder =
                new HasParentQueryBuilder(GoodsRelationField.GOODS_STORE, QueryBuilders.matchAllQuery(), false)
                        .innerHit(new InnerHitBuilder());

        // 查询商品spu的子类型，商品sku信息
        HasChildQueryBuilder hasChildQueryBuilder =
                new HasChildQueryBuilder(GoodsRelationField.GOODS_SKU, QueryBuilders.matchAllQuery(), ScoreMode.None)
                        .innerHit(new InnerHitBuilder());

        // bool查询构建器
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(idsQueryBuilder).must(hasParentQueryBuilder).must(hasChildQueryBuilder);

        // 查询资源构建器
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQueryBuilder);
        return sourceBuilder;
    }

    /**
     * 构建根据id查询商品spu详情的查询结果
     *
     * @param searchResponse es查询响应
     * @return               查询结果
     */
    private GoodsSpuDetail buildSpuDetailByIdResult(SearchResponse searchResponse) {
        // 因为是根据id查询，所以结果只有一个
        SearchHit hit = searchResponse.getHits().getHits()[0];
        GoodsSpuDetail goodsSpuDetail = JSON.parseObject(hit.getSourceAsString(), GoodsSpuDetail.class);

        // 获取店铺信息和商品sku查询结果
        Map<String, SearchHits> innerHits = hit.getInnerHits();
        // hasParent查询结果，即店铺信息
        SearchHits parentSearchHits = innerHits.get(GoodsRelationField.GOODS_STORE);
        // hasParent只能有一条记录，所以直接取下标为0的数据
        GoodsStore goodsStore = JSON.parseObject(parentSearchHits.getHits()[0].getSourceAsString(), GoodsStore.class);
        goodsSpuDetail.setStoreId(goodsStore.getId());
        goodsSpuDetail.setStoreName(goodsStore.getStoreName());

        // hasChild查询结果，即商品sku信息
        SearchHits childSearchHits = innerHits.get(GoodsRelationField.GOODS_SKU);
        List<GoodsSku> goodsSkuList = new ArrayList<>();
        for (SearchHit searchHit : childSearchHits) {
            GoodsSku goodsSku = JSON.parseObject(searchHit.getSourceAsString(), GoodsSku.class);
            goodsSkuList.add(goodsSku);
        }
        goodsSpuDetail.setGoodsSkuList(goodsSkuList);
        return goodsSpuDetail;
    }

}
