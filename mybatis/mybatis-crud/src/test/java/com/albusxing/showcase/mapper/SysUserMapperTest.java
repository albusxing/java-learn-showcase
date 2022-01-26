package com.albusxing.showcase.mapper;
import com.albusxing.showcase.model.SysUser;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SysUserMapperTest {

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


    /*********************  测试查询  ***************/

    /**
     * 查询所有数据
     */
    @Test
    public void test_selectAll() {
        SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
        List<SysUser> sysUsers = mapper.selectAll();
        System.out.println(sysUsers);
    }

    /**
     * 模糊查询用户名
     */
    @Test
    public void test_selectLikeUserName() {
        SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
        List<SysUser> sysUsers = mapper.selectLikeUserName("user");
        System.out.println(sysUsers);
    }

    /**
     * 根据主键查询
     */
    @Test
    public void test_selectById() {
        SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
        SysUser sysUser = mapper.selectById(1L);
        System.out.println(sysUser);
    }



    /*********************  测试新增 ***************/


    /**
     * 新增一条记录，没有返回主键
     */
    @Test
    public void test_insert() {
        SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);

        SysUser user = new SysUser();
        user.setUserName("ligq");
        user.setUserPassword("123456");
        user.setUserEmail("ligq@gmail.com");
        user.setUserInfo("test userinfo");
        user.setHeadImg(new byte[]{1, 2, 3});
        user.setCreateTime(new Date());

        // 这个返回的是执行的行数
        int insert = mapper.insert(user);
        if (insert == 1) {
            System.out.println("新增一条用户信息");
            System.out.println("新增的数据主键是：" + user.getId());
        }
        sqlSession.commit();
    }


    /**
     * 新增一条记录，并返回主键，使用 useGeneratedKeys
     */
    @Test
    public void test_insert_useGeneratedKeys() {
        SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);

        SysUser user = new SysUser();
        user.setUserName("ligq2");
        user.setUserPassword("123456");
        user.setUserEmail("ligq@gmail.com");
        user.setUserInfo("test userinfo");
        user.setHeadImg(new byte[]{1, 2, 3});
        user.setCreateTime(new Date());

        // 这个返回的是执行的行数
        int insert = mapper.insert_useGeneratedKeys(user);
        if (insert == 1) {
            System.out.println("新增一条用户信息");
            System.out.println("新增的数据主键是：" + user.getId());
        }
        sqlSession.commit();
    }


    /**
     * 新增一条记录，并返回主键，使用 selectKey
     */
    @Test
    public void test_insert_selectKey() {
        SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);

        SysUser user = new SysUser();
        user.setUserName("ligq3");
        user.setUserPassword("123456");
        user.setUserEmail("ligq@gmail.com");
        user.setUserInfo("test userinfo");
        user.setHeadImg(new byte[]{1, 2, 3});
        user.setCreateTime(new Date());

        // 这个返回的是执行的行数
        int insert = mapper.insert_selectKey(user);
        if (insert == 1) {
            System.out.println("新增一条用户信息");
            System.out.println("新增的数据主键是：" + user.getId());
        }
        sqlSession.commit();
    }


    /*********************  测试修改 ***************/

    /**
     * 根据主键修改对象
     */
    @Test
    public void testUpdateById() {
        SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);

    /*
        SysUser user = new SysUser();
        user.setId(1039L);
        user.setUserName("ligq3-update");
        user.setUserPassword("123456-update");
    */

        // 先查询再更新
        SysUser user = mapper.selectById(1074L);
        user.setUserInfo("updated UserInfo");

        // 这个返回的是执行的行数
        int insert = mapper.updateById(user);
        if (insert == 1) {
            System.out.println("修改一条用户信息");
        }
        sqlSession.commit();
    }


    /*********************  测试删除 ***************/

    @Test
    public void testDeleteById() {
        SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);

        // 这个返回的是执行的行数
        int insert = mapper.deleteById(1073L);
        if (insert == 1) {
            System.out.println("删除了一条用户信息");
        }

        SysUser user = mapper.selectById(1074L);
        int num = mapper.deleteById(user);
        if (num == 1) {
            System.out.println("又删除了一条用户信息");
        }

        sqlSession.commit();
    }


    /*************** 动态sql *********************/

    @Test
    public void selectByUserNameOrEmail() {

        SysUserMapper userMapper = sqlSession.getMapper(SysUserMapper.class);

        SysUser query = new SysUser();
        // 只根据 用户名查询
//        query.setUserName("ad");

        // 只根据 用户邮箱查询
//        query.setUserEmail("gmail");CuoFeeCompTaskManager

        // 根据 用户名和邮箱 查询
        query.setUserName("li");
        query.setUserEmail("gmail");

        List<SysUser> sysUsers = userMapper.selectByUserNameOrEmail(query);
        System.out.println(sysUsers);
    }

    @Test
    public void selectByIdOrUserName() {
        SysUserMapper userMapper = sqlSession.getMapper(SysUserMapper.class);
        SysUser query = new SysUser();
        // 根据 用户名和邮箱 查询
//        query.setUserName("admin");
//        query.setId(1l);
//        SysUser sysUser = userMapper.selectByIdOrUserName(query);
//        System.out.println(sysUser);

        // 只根据 用户id查询
//        query.setUserName(null);
//        query.setId(1l);
//        SysUser sysUser = userMapper.selectByIdOrUserName(query);
//        System.out.println(sysUser);

        // 只根据 用户名查询
        query.setUserName("admin");
        query.setId(null);
        SysUser sysUser = userMapper.selectByIdOrUserName(query);
        System.out.println(sysUser);


    }


    @Test
    public void selectByIdList() {
        SysUserMapper userMapper = sqlSession.getMapper(SysUserMapper.class);

        List<Long> ids = new ArrayList<Long>();
//        ids.add(1l);
//        ids.add(1001l);
//        ids.add(1036l);
//        ids.add(1037l);
        List<SysUser> sysUsers = userMapper.selectByIdList(ids);
        System.out.println(sysUsers);
    }




    @Test
    public void deleteByIdList() {
        SysUserMapper userMapper = sqlSession.getMapper(SysUserMapper.class);

        List<Long> ids = new ArrayList<Long>();
        ids.add(1l);
        ids.add(1001l);
        ids.add(1036l);
        ids.add(1037l);
        int row = userMapper.deleteByIdList(ids);
        System.out.println("删除的记录数：" + row);
    }

    @Test
    public void updateByIdSelective() {

        SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);

        SysUser user = new SysUser();
        user.setId(1037L);
        user.setUserName("albus");
        user.setUserPassword("111111");
        // 这个返回的是执行的行数
        int insert = mapper.updateByIdSelective(user);
        if (insert == 1) {
            System.out.println("修改一条用户信息");
        }

        sqlSession.commit();

    }

    @Test
    public void insertSelective() {
        SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
        SysUser user = new SysUser();
        user.setUserName("test-selective");
        user.setUserPassword("123456");
        user.setUserInfo("test info");
        user.setCreateTime(new Date());
        // 这个返回的是执行的行数
        int insert = mapper.insertSelective(user);
        if (insert == 1) {
            System.out.println("新增一条用户信息");
        }
        sqlSession.commit();
    }


    @Test
    public void insertByList() {
        List<SysUser> list = new ArrayList<SysUser>();
        for (int i = 0; i < 10; i++) {
            SysUser user = new SysUser();
            user.setUserName("1108-user" + i);
            user.setUserPassword("1108");
            user.setUserInfo("1108 info" + i);
            user.setCreateTime(new Date());
            list.add(user);
        }
        SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
        mapper.insertByList(list);
        System.out.println(list);

        sqlSession.commit();
    }

}
