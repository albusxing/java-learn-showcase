package com.albusxing.basis.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.naixue.common.api.ApiResponse;
import com.naixue.common.api.ResponseInfo;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * token认证
 *
 * @author kojon
 * @date 2020/4/17
 */
//@Component
public class AuthFilter implements GlobalFilter {

    //认证体系 接口鉴权
    //JWT
    //应用管理 去发放appid+appkey
    //不同的接入方 使用的key都不同

    //客户端按下面要求处理加密
    //mD5(请求body体+时间戳+随机数+appid+appkey)+sign
    //后端同样的方式加密  然后对比最后的签名是否一致
    //时间戳 和服务器差别有500ms 那说明可能是暴力破解
    //随机数长度 用来增强破解难度
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> token = exchange.getRequest().getHeaders().get("token");
        //大家可以自己去实践api鉴权
        if (CollectionUtils.isEmpty(token)) {
            ServerHttpResponse response = exchange.getResponse();
            String authErrorStr = JSON.toJSONString(ApiResponse.of(ResponseInfo.AUTH_ERROR));
            response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            DataBuffer body = response.bufferFactory().wrap(authErrorStr.getBytes());
            return response.writeWith(Mono.just(body));
        }
        return chain.filter(exchange);
    }
}
