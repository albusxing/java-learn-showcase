package com.albusxing.showcase.mapper;


import com.albusxing.showcase.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liguoqing
 */
public interface SysUserMapper {

    /**
     * 通过 id 查询用户
     */
    SysUser selectById(Long id);
    /**
     * 查询全部用户
     */
    List<SysUser> selectAll();

    /**
     * 根据用户名模糊查询
     */
    List<SysUser> selectLikeUserName(@Param("userName") String userName);

    /**
     * 新增用户
     */
    int insert(SysUser sysUser);
    /**
     * 新增用户 - 使用 useGeneratedKeys 方式
     */
    int insert_useGeneratedKeys(SysUser sysUser);

    /**
     * 新增用户 - 使用 selectKey 方式
     */
    int insert_selectKey(SysUser sysUser);
    /**
     * 根据主键更新
     * 这种修改方式对象中为 null 的属性，数据库也会变成 null
     * 使用这个的前提是 先查询出数据，再进行修改
     */
    int updateById(SysUser sysUser);


    //  方法名相同的重载方法，调用的 映射文件里面的同一个sql语句 //
    /**
     * 通过主键删除
     */
    int deleteById(Long id);
    /**
     * 通过主键删除
     */
    int deleteById(SysUser sysUser);



    /**
     * 根据动态条件(用户名称和用户邮箱)
     * 查询用户信息
     */
    List<SysUser> selectByUserNameOrEmail(SysUser sysUser);

    /**
     * 根据用户id or 用户 name 动态查找
     */
    SysUser selectByIdOrUserName(SysUser sysUser);

    /**
     * 根据 id 集合查询 使用foreach
     * 批量查询
     */
    List<SysUser> selectByIdList(@Param("idList") List<Long> idList);


    /**
     * 根据主键更新
     * 动态根据 条件对象SysUser 判断是否更新
     * 属性不为空就更新，为空就bu更新
     */
    int updateByIdSelective(SysUser sysUser);


    /**
     * 动态插入数据
     * 如果保存实体属性为null，就使用数据库默认值保存
     * @param sysUser
     */
    int insertSelective(SysUser sysUser);

    /**
     * 批量插入
     * @param sysUserList
     */
    int insertByList(@Param("sysUserList") List<SysUser> sysUserList);

    /**
     * 批量删除
     * @param idList
     * @return
     */
    int deleteByIdList(@Param("idList") List<Long> idList);


}
