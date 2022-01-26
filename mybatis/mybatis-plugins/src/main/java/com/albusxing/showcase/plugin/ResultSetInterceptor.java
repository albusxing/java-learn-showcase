package com.albusxing.showcase.plugin;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.Properties;

/**
 * 拦截结果集
 *
 * @author liguoqing
 * @date 2019-08-27
 */
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class ResultSetInterceptor implements Interceptor {


    /**
     * 实际的拦截方法
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("======> 对 ResultSetHandler # handleResultSets 方法进行了拦截");
        Object target = invocation.getTarget();
        Method method = invocation.getMethod();
        Object[] args = invocation.getArgs();

        return invocation.proceed();
    }


    /**
     * 设置拦截目标对象
     *
     * @param target
     * @return
     */
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 为拦截起设置初始化属性
     * 直接从配置中获取参数
     *
     * @param properties
     */
    public void setProperties(Properties properties) {

    }
}
