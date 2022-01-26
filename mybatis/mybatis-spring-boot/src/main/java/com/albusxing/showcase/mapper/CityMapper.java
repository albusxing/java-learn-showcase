package com.albusxing.showcase.mapper;


import com.albusxing.showcase.domain.City;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author liguoqing
 * @date 2019-08-19
 */
@Mapper
public interface CityMapper {


    /**
     * 查询所有
     * @return
     */
    @Select("select * from t_city")
    List<City> selectAll();

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @Select("select * from t_city where id = #{id}")
    City selectById(Long id);

}
