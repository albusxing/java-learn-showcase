package com.albusxing.showcase.domain;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author liguoqing
 * @date 2019-08-19
 */
@Data
@ToString
public class City implements Serializable {

    private Long id;

    private String name;

    private String state;

    private String country;

}
