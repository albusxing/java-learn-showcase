package com.albusxing.showcase.domain;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author liguoqing
 * @date 2019-08-19
 */
@ToString
@Data
public class Country implements Serializable {

    private Long id;
    private String countryCode;
    private String countryName;

}
