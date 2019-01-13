package com.pinyougou.page.service.impl;


import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class PageListener implements MessageListener {


    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        try {

            System.out.println("shou dao xiao xi !!");
            ObjectMessage objectMessage = (ObjectMessage) message;
            Long[] ids  = (Long[]) objectMessage.getObject();
            for (Long id : ids) {
                itemPageService.genItemHtml(id);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
