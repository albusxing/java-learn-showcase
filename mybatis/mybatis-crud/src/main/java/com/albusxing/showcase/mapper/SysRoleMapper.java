package com.albusxing.showcase.mapper;


import com.albusxing.showcase.model.SysRole;
import com.albusxing.showcase.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {

    /**
     * 根据 用户id 查询查询用户角色
     */
    List<SysRole> selectRolesByUserId(Long userId);
    /**
     * 根据 userid 和 enable
     */
    List<SysRole> selectRolesByUserIdAndRoleEnabled(@Param("userId") Long userId,
                                                    @Param("enabled") Integer enabled);
    /**
     * 根据 userid 和 enable
     */
    List<SysRole> selectRolesByUserAndRole(@Param("user") SysUser user,
                                           @Param("role") SysRole role);


}
