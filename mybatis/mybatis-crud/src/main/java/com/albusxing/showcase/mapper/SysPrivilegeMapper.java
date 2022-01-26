package com.albusxing.showcase.mapper;

import com.albusxing.showcase.model.SysPrivilege;
import org.apache.ibatis.annotations.*;

/**
 * @author liguoqing
 */
public interface SysPrivilegeMapper {


    @Select("select id, privilege_name, privilege_url from sys_privilege where id= #{id} ")
	SysPrivilege selectPrivilegeById(@Param("id") Long id);

    @Select("select count(1) from sys_privilege")
    int count();

    @Insert("INSERT INTO sys_privilege (privilege_name, privilege_url) VALUES (#{privilegeName}, #{privilegeUrl})")
    int insertPrivilege(SysPrivilege sysPrivilege);

    @Insert("INSERT INTO sys_privilege (privilege_name, privilege_url) VALUES (#{privilegeName}, #{privilegeUrl})")
    @SelectKey(statement = "select last_insert_id()", keyColumn = "id", keyProperty = "id", before = false, resultType = Long.class)
    int insertPrivilegeAndReturnPk(SysPrivilege sysPrivilege);

    @Update("UPDATE sys_privilege SET privilege_name = #{privilegeName}, privilege_url = #{privilegeUrl} where id = #{id} ")
    int updatePrivilege(SysPrivilege sysPrivilege);

    @Delete("DELETE FROM sys_privilege where id = #{id}")
    int deletePrivilege(@Param("id") Long id);


}
