package com.dddframework.monitor.application.service;


import com.dddframework.monitor.domain.qiwei.service.QiWeiService;

/**
 * 企微机器人
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
public class QiWeiRobotSender implements Sender {

    private final String key;

    public QiWeiRobotSender(String key) {
        this.key = key;
    }

    @Override
    public void send(String msg) {
        QiWeiService.send(key, msg);
    }
}
