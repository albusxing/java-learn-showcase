package com.albusxing.showcase.biz.common.base;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * @author liguoqing
 */
@Data
public class TableData<T> implements Serializable {

    /**
     * 总条数
     */
    private Long total;

    /**
     * 数据行
     */
    private List<T> rows;


}
