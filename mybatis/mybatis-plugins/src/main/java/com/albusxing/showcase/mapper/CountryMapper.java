package com.albusxing.showcase.mapper;

import com.albusxing.showcase.domain.Country;

import java.util.List;

/**
 * @author liguoqing
 * @date 2019-08-19
 */
public interface CountryMapper {


    /**
     * 查询所有
     *
     * @return
     */
    List<Country> selectAll();


}
