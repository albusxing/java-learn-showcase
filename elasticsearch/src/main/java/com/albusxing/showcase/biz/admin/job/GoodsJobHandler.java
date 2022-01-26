package com.albusxing.showcase.biz.admin.job;

import com.albusxing.elasticsearch.showcase.biz.admin.service.AdminGoodsService;
import com.albusxing.elasticsearch.showcase.biz.common.constant.StringConstant;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 商品自动上架job
 * @author liguoqing
 */
@Component
public class GoodsJobHandler {

    @Autowired
    private AdminGoodsService adminGoodsService;

    /**
     * 自动上架商品job
     */
    @XxlJob(value = "autoOnlineGoodsJobHandler")
    public ReturnT<String> autoOnlineGoodsJobHandler(String param) throws Exception {
        XxlJobLogger.log("开始执行商品自动上架定时任务");
        try {
            // 不采用分页的方式自动上架商品
//            adminGoodsService.autoOnlineGoodsSpuByOnlineTime(
//                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(StringConstant.FULL_DATE_TIME)));

            // 采用scroll分页的方式自动上架商品
            adminGoodsService.autoOnlineGoodsSpuByOnlineTimeScroll(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(StringConstant.FULL_DATE_TIME)));

            XxlJobLogger.log("执行商品自动上架定时任务完成");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行商品自动上架定时任务出错");
        }
        return ReturnT.FAIL;
    }
}

