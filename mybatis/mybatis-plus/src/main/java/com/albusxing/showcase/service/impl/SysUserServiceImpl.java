package com.albusxing.showcase.service.impl;


import com.albusxing.showcase.entity.SysUser;
import com.albusxing.showcase.mapper.SysUserMapper;
import com.albusxing.showcase.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author albusxing
 * @since 2020-11-12
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

}
