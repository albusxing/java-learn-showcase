package com.albusxing.showcase.mapper;

import com.albusxing.showcase.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class CountryMapperTest {

    @Resource
    private CountryMapper countryMapper;


    @Test
    public void test_crud() {
        // 此处 null 指的是不用根据参数去查询
        // 可以调用 CRUD 相关的多种方式

        // 1. 查询所有的数据
        List<Country> countryList = countryMapper.selectList(null);
        countryList.forEach(System.out::println);

        // 2. 根据 id 删除
//        countryMapper.deleteById(1L);
//
//        // 3. 添加数据
//        Country country = new Country();
//        country.setCode("111");
//        country.setName("上海");
//        countryMapper.insert(country);
//
//
//        // 4. 更新数据（此时插入的是）
//        country.setCode("001");
//        country.setName("美国");
//        countryMapper.updateById(country);
    }




}