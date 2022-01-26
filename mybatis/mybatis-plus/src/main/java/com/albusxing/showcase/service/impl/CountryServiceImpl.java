package com.albusxing.showcase.service.impl;

import com.albusxing.showcase.entity.Country;
import com.albusxing.showcase.mapper.CountryMapper;
import com.albusxing.showcase.service.ICountryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author albusxing
 * @since 2020-11-13
 */
@Service
public class CountryServiceImpl extends ServiceImpl<CountryMapper, Country> implements ICountryService {

}
