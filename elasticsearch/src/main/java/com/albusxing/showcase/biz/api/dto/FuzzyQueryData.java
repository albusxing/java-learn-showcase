package com.albusxing.showcase.biz.api.dto;
import com.albusxing.showcase.biz.common.base.TableData;
import lombok.Data;

@Data
public class FuzzyQueryData<T> extends TableData<T> {

    /**
     * 查询内容
     */
    private String queryContent;

    /**
     * 模糊查询之后的内容
     */
    private String fuzzyContent;

}
