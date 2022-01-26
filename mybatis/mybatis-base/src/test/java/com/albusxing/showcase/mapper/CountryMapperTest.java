package com.albusxing.showcase.mapper;

import com.albusxing.showcase.model.Country;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class CountryMapperTest {

    private static SqlSession sqlSession;

    @Before
    public void init() {
        try {
            // 读取 mybatis 配置文件
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            // 构建 SqlSessionFactory
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            // 创建会话
            sqlSession = sqlSessionFactory.openSession();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void close() {
        if (sqlSession != null) {
            sqlSession.close();
        }
    }


    /**
     * 使用 SqlSession 对象的方法进行数据查询
     */
    @Test
    public void test_selectAll() {
        List<Country> countryList = sqlSession.selectList("selectAll");
        System.out.println(countryList);
    }


    /**
     * 使用 Mapper 接口对象的方法进行数据查询
     */
    @Test
    public void test_selectAll2() {
        // 获取Mapper
        CountryMapper countryMapper = sqlSession.getMapper(CountryMapper.class);
        // 执行sql
        List<Country> countries = countryMapper.selectAll();
        System.out.println(countries);
    }
}