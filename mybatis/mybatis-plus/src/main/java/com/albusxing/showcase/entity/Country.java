package com.albusxing.showcase.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author albusxing
 * @since 2020-11-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Country implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private String name;


}
