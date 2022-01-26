package com.albusxing.showcase.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liguoqing
 */
@Data
public class SysRole implements Serializable {

    /**
     * 角色ID
     */
    private Long id;
    /**
     * 角色名
     */
    private String roleName;
    /**
     * 有效标志
     * 0 无效 1 有效
     */
    private Integer enabled;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户信息
     * 冗余关联对象
     */
    private SysUser user;


}
