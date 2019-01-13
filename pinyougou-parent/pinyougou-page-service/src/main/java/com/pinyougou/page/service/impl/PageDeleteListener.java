package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Service
public class PageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        ObjectMessage objectMessage= (ObjectMessage)message;
        try {
            Long[] goodsIds = (Long[]) objectMessage.getObject();
            System.out.println("ItemDeleteListener  shou dao xiao xi...");
            boolean b = itemPageService.deleteItemHtml(goodsIds);
            System.out.println("shanchu  jieguo："+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
