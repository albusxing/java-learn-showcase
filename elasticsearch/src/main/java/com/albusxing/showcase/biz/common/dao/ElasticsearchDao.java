package com.albusxing.showcase.biz.common.dao;


import com.albusxing.showcase.biz.common.base.CommonResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.core.io.Resource;
import java.io.IOException;

/**
 * @author liguoqing
 */
public interface ElasticsearchDao {


    /**
     * 判断索引是否存在
     */
    Boolean existIndex(String indexName) throws IOException;

    /**
     * 创建索引
     */
    Boolean createIndex(String indexName, String aliasName, Resource mappingResource) throws IOException;

    /**
     * 判断文档是否存在
     */
    Boolean existDoc(String indexName, String id) throws IOException;

    /**
     * 新增数据
     */
    CommonResponse insert(IndexRequest indexRequest);

    /**
     * 修改数据
     */
    CommonResponse update(UpdateRequest updateRequest);

    /**
     * 删除数据
     */
    CommonResponse delete(DeleteRequest deleteRequest);


    /**
     * 批量操作数据
     */
    CommonResponse bulk(BulkRequest bulkRequest);


    /**
     * 根据id查询结果
     */
    <T> T getById(String index, String id, Class<T> clazz);

    /**
     * 将源索引数据写入到目标索引中
     * @param oldIndexName 源索引名称
     * @param newIndexName 目标索引
     * @throws IOException
     */
    void reindex(String oldIndexName, String newIndexName) throws IOException;

    /**
     * 给索引添加索引别名
     * @param indexName 索引名称
     * @param aliasName 索引别名名称
     * @return 是否成功
     * @throws IOException
     */
    Boolean addAlias(String indexName, String aliasName) throws IOException;

    /**
     * 将索引别名指向的旧索引替换成新的索引
     * @param aliasName     索引别名
     * @param oldIndexName  旧索引名称
     * @param newIndexName  新索引名称
     * @return              是否成功
     * @throws IOException
     */
    Boolean changeAliasAfterReindex(String aliasName, String oldIndexName, String newIndexName) throws IOException;


}
