package com.albusxing.showcase;

import com.albusxing.showcase.mapper.CityMapper;
import com.albusxing.showcase.domain.City;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class MyBatisApplicationTest {


    @Resource
    private CityMapper cityMapper;


    @Test
    public void test_selectAll() {
        List<City> cities = cityMapper.selectAll();
        System.out.println(cities);
    }


    @Test
    public void test_selectById() {
        City city = cityMapper.selectById(8L);
        System.out.println(city);
    }

}