package com.albusxing.showcase.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 权限表
 * </p>
 *
 * @author albusxing
 * @since 2020-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysPrivilege implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限名称
     */
    private String privilegeName;

    /**
     * 权限URL
     */
    private String privilegeUrl;


}
