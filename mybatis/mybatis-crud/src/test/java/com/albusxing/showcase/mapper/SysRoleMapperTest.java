package com.albusxing.showcase.mapper;


import com.albusxing.showcase.mapper.SysRoleMapper;
import com.albusxing.showcase.model.SysRole;
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

public class SysRoleMapperTest {

    private static SqlSession sqlSession;


    @Before
    public void init() {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
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


    @Test
    public void testSelectRolesByUserId() {
        SysRoleMapper mapper = sqlSession.getMapper(SysRoleMapper.class);
        List<SysRole> sysRoles = mapper.selectRolesByUserId(1L);
        System.out.println(sysRoles);
    }


    @Test
    public void selectRolesByUserIdAndRoleEnabled() {
        SysRoleMapper mapper = sqlSession.getMapper(SysRoleMapper.class);
        List<SysRole> sysRoles = mapper.selectRolesByUserIdAndRoleEnabled(1L, 1);
        System.out.println(sysRoles);
    }


}
