package com.albusxing.showcase.model;


import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author liguoqing
 */
@Data
@ToString
public class SysPrivilege implements Serializable {

    private Long id;
    private String privilegeName;
    private String privilegeUrl;
}
