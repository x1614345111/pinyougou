package com.pinyougou.util;


import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    private void importItemData(){

        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = itemMapper.selectByExample(example);

        for (TbItem tbItem : tbItems) {
            Map specMap = JSON.parseObject(tbItem.getSpec());
            tbItem.setSpecMap(specMap);

            System.out.println(tbItem.getBrand());
        }

        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }



    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");

        SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
        solrUtil.importItemData();
    }




}
