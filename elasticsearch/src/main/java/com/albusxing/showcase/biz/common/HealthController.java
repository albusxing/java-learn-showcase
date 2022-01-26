package com.albusxing.showcase.biz.common;


import com.albusxing.elasticsearch.showcase.biz.common.base.CommonResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liguoqing
 */
@RestController
public class HealthController {

    @GetMapping("/ok")
    public CommonResponse health() {
        return CommonResponse.success();
    }
}
