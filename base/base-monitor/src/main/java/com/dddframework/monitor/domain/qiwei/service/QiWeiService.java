package com.dddframework.monitor.domain.qiwei.service;

import com.dddframework.core.utils.JsonKit;
import com.dddframework.monitor.domain.contract.dto.MarkDownDTO;
import com.dddframework.monitor.domain.contract.dto.MsgDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * 企微告警util
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
@Slf4j(topic = "### BASE-MONITOR : QiWeiService ###")
public class QiWeiService {

    private static final RestTemplate restTemplate = new RestTemplate();


    public static final String BASE_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=";

    /**
     * 处理发送的钉钉消息
     *
     * @param msg json格式数据
     */
    public static void send(String key, String msg) {
        Long timestamp = System.currentTimeMillis();
        String dingUrl = BASE_URL + key;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
        MsgDTO content = new MsgDTO();
        MarkDownDTO markDown = new MarkDownDTO();
        markDown.setContent(msg);
        content.setMarkdown(markDown);
        content.setMsgtype("markdown");
        HttpEntity<String> httpEntity = new HttpEntity<>(JsonKit.toJson(content), headers);
        try {
            String response = restTemplate.postForObject(dingUrl, httpEntity, String.class);
            log.debug("【发送企微告警消息】消息响应结果：{}", response);
        } catch (Exception e) {
            log.error("【发送企微告警消息】error：" + e.getMessage(), e);
        }
    }


}