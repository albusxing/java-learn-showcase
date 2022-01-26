package com.albusxing.showcase.mapper;


import com.albusxing.showcase.mapper.SysPrivilegeMapper;
import com.albusxing.showcase.model.SysPrivilege;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

public class SysPrivilegeMapperTest {


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
    public void test_SelectById() {
        SysPrivilegeMapper mapper = sqlSession.getMapper(SysPrivilegeMapper.class);
        SysPrivilege sysPrivilege = mapper.selectPrivilegeById(1L);
        System.out.println(sysPrivilege);
    }

    @Test
    public void test_count() {
        SysPrivilegeMapper mapper = sqlSession.getMapper(SysPrivilegeMapper.class);
        int sum = mapper.count();
        System.out.println("sum = " + sum);
    }


    @Test
    public void test_insertPrivilege() {
        SysPrivilegeMapper mapper = sqlSession.getMapper(SysPrivilegeMapper.class);
        SysPrivilege sysPrivilege = new SysPrivilege();
        sysPrivilege.setPrivilegeName("测试链接");
        sysPrivilege.setPrivilegeUrl("/test/url");
        int row = mapper.insertPrivilege(sysPrivilege);
        if (row == 1) {
            System.out.println(">>>>> 新增一条记录");
            System.out.println(sysPrivilege);
        }

        sqlSession.commit();

    }


    @Test
    public void test_insertPrivilegeAndReturnPk() {
        SysPrivilegeMapper mapper = sqlSession.getMapper(SysPrivilegeMapper.class);
        SysPrivilege sysPrivilege = new SysPrivilege();
        sysPrivilege.setPrivilegeName("测试链接");
        sysPrivilege.setPrivilegeUrl("/test/url");
        int row = mapper.insertPrivilegeAndReturnPk(sysPrivilege);
        if (row == 1) {
            System.out.println(">>>>> 新增一条记录");
            System.out.println(sysPrivilege);
        }
        sqlSession.commit();
    }

    @Test
    public void test_updatePrivilege() {
        SysPrivilegeMapper mapper = sqlSession.getMapper(SysPrivilegeMapper.class);

        SysPrivilege sysPrivilege = mapper.selectPrivilegeById(9L);
        System.out.println("查询出来的数据=" + sysPrivilege);
        sysPrivilege.setPrivilegeName("更新测试链接");
        sysPrivilege.setPrivilegeUrl("/test/update/url");
        int row = mapper.updatePrivilege(sysPrivilege);
        if (row == 1) {
            System.out.println(">>>>> 更新一条记录");
            System.out.println(sysPrivilege);
        }
        sqlSession.commit();
    }


    @Test
    public void test_deletePrivilege() {
        SysPrivilegeMapper mapper = sqlSession.getMapper(SysPrivilegeMapper.class);
        int row = mapper.deletePrivilege(9L);
        if (row == 1) {
            System.out.println(">>>>> 删除一条记录");
        }
        sqlSession.commit();
    }



}