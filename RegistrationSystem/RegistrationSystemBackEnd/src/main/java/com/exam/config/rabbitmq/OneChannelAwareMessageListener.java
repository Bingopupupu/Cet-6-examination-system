package com.exam.config.rabbitmq;

import com.exam.dao.ExamUserDao;
import com.exam.pojo.param.ExamUserParam;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gaoge
 * @since 2023/2/10 11:31
 */
@Slf4j
@Component("oneChannelAwareMessageListener")
public class OneChannelAwareMessageListener implements ChannelAwareMessageListener {
    private final static Gson g= new Gson();
    @Autowired
    private ExamUserDao examUserDao;
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String s = new String(message.getBody());
        ExamUserParam examUserParam = g.fromJson(s, ExamUserParam.class);
        examUserDao.update(examUserParam);
        log.info("容器监听到："+s);
    }
}
