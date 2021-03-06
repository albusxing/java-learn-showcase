package com.albusxing.showcase.biz.common.dao.impl;

import com.albusxing.showcase.biz.common.base.CommonResponse;
import com.albusxing.showcase.biz.common.constant.StringConstant;
import com.albusxing.showcase.biz.common.dao.ElasticsearchDao;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author liguoqing
 */
@Slf4j
@Component
public class ElasticsearchDaoImpl implements ElasticsearchDao {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean existIndex(String indexName) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        log.info("???????????????{}????????????????????????{}", indexName, exists);
        return exists;
    }

    @Override
    public Boolean createIndex(String indexName, String aliasName, Resource mappingResource) throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        if (StringUtils.hasText(aliasName)) {
            createIndexRequest.alias(new Alias(aliasName));
        }
        String indexMappingJson = IOUtils.toString(mappingResource.getInputStream());
        createIndexRequest.mapping(indexMappingJson, XContentType.JSON);

        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        log.info("???????????????{}????????????{}????????????{}", indexName, aliasName, JSON.toJSONString(createIndexResponse));
        return createIndexResponse.isAcknowledged();
    }

    @Override
    public Boolean existDoc(String indexName, String id) throws IOException {
        GetRequest getRequest = new GetRequest(indexName,id);
        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        log.info("??????:{}?????????????????????id???{}??? ?????????{}", indexName, id, exists);
        return exists;
    }

    @Override
    public CommonResponse insert(IndexRequest indexRequest) {
        try {
            IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            log.info("????????????{}", indexResponse.status());
            if (indexResponse.status() == RestStatus.CREATED) {
                return CommonResponse.success(indexResponse.getId());
            }
        } catch (Exception e) {
            log.error("????????????", e);
        }
        return CommonResponse.fail();
    }

    @Override
    public CommonResponse update(UpdateRequest updateRequest) {
        try {
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            log.info("????????????{}", updateResponse.status());
            if (updateResponse.status() == RestStatus.OK) {
                return CommonResponse.success();
            }
        } catch (Exception e) {
            log.error("????????????", e);
        }
        return CommonResponse.fail();
    }

    @Override
    public CommonResponse delete(DeleteRequest deleteRequest) {
        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            if (deleteResponse.status() == RestStatus.OK) {
                return CommonResponse.success();
            }
        } catch (Exception e) {
            log.error("????????????", e);
        }
        return CommonResponse.fail();
    }

    @Override
    public CommonResponse bulk(BulkRequest bulkRequest) {
        log.info("??????????????????bulk????????????????????????{}", JSON.toJSONString(bulkRequest));
        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            log.info("??????????????????bulk???????????????{}", JSON.toJSONString(bulkResponse));
            if (bulkResponse.status() == RestStatus.OK) {
                return CommonResponse.success();
            }
        } catch (IOException e) {
            log.error("??????????????????bulk????????????", e);
        }
        return CommonResponse.fail();
    }

    @Override
    public <T> T getById(String index, String id, Class<T> clazz) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(new IdsQueryBuilder().addIds(id));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(sourceBuilder);
        try {
            log.info("ES???????????????{}", searchRequest.source().toString());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            if (searchHits != null && searchHits.length == 1) {
                return JSON.parseObject(searchHits[0].getSourceAsString(), clazz);
            }
        } catch (IOException e) {
            log.error("??????id??????????????????", e);
        }
        return null;
    }

    @Override
    public void reindex(String oldIndexName, String newIndexName) throws IOException {
        ReindexRequest request = new ReindexRequest();
        // ???index
        request.setSourceIndices(oldIndexName);
        // ??????index
        request.setDestIndex(newIndexName);
        // scroll????????????????????????
        request.setSourceBatchSize(1000);
        // ????????? create ??? index?????????index????????????????????????create???????????????????????????
        request.setDestOpType(StringConstant.CREATE);
        // ????????? proceed ??? abort???proceed??????????????????abort???????????????
        request.setConflicts(StringConstant.PROCEED);
        // ???????????????????????????refresh
        request.setRefresh(true);
        restHighLevelClient.reindex(request, RequestOptions.DEFAULT);
        log.info("reindex finished, oldIndex: {} newIndex: {}", oldIndexName, newIndexName);
    }

    @Override
    public Boolean addAlias(String indexName, String aliasName) throws IOException {
        IndicesAliasesRequest aliasesRequest = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions aliasAction =
                new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                        .index(indexName)
                        .alias(aliasName);
        aliasesRequest.addAliasAction(aliasAction);
        AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices().updateAliases(aliasesRequest,RequestOptions.DEFAULT);
        log.info("add index alias , index: {} alias: {} response: {}", indexName, aliasName, acknowledgedResponse.isAcknowledged());
        return acknowledgedResponse.isAcknowledged();
    }

    @Override
    public Boolean changeAliasAfterReindex(String aliasName, String oldIndexName, String newIndexName) throws IOException {
        // ??????????????????
        IndicesAliasesRequest.AliasActions addIndexAction = new IndicesAliasesRequest.AliasActions(
                IndicesAliasesRequest.AliasActions.Type.ADD).index(newIndexName).alias(aliasName);
        // ??????????????????
        IndicesAliasesRequest.AliasActions removeAction = new IndicesAliasesRequest.AliasActions(
                IndicesAliasesRequest.AliasActions.Type.REMOVE).index(oldIndexName).alias(aliasName);

        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        indicesAliasesRequest.addAliasAction(addIndexAction);
        indicesAliasesRequest.addAliasAction(removeAction);
        AcknowledgedResponse indicesAliasesResponse = restHighLevelClient.indices().updateAliases(indicesAliasesRequest,
                RequestOptions.DEFAULT);
        log.info("change index alias , alias: {}, remove index: {}, add index: {}, response: {}", aliasName,
                oldIndexName, newIndexName, indicesAliasesResponse.isAcknowledged());
        return indicesAliasesResponse.isAcknowledged();
    }
}

