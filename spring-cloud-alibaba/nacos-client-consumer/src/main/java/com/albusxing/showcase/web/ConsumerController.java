package com.albusxing.showcase.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Resource;

/**
 * @author Albusxing
 */
@RestController
public class ConsumerController {

    private Logger logger = LoggerFactory.getLogger(ConsumerController.class);

    @Resource
    private LoadBalancerClient loadBalancerClient;
    @Resource
    private RestTemplate restTemplate;


    @GetMapping("/consumer/msg")
    public String getProviderMessage() {

        ServiceInstance serviceInstance = loadBalancerClient.choose("nacos-client-provider");
        String host = serviceInstance.getHost();
        int port = serviceInstance.getPort();
        logger.info("本次调用【nacos-client-provider】host={}, port={}", host, port);
        String url = "http://" + host + ":" + port + "/provider/msg";
        String result = restTemplate.getForObject(url, String.class);
        logger.info("【nacos-client-provider】响应数据：{}", result);
        return "【nacos-client-provider】响应数据：" + result;
    }




}
