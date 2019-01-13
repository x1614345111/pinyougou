package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ItemPageServiceImpl implements ItemPageService {

    //没有加载文件目录，是因为 dao中已经加载了
    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public boolean genItemHtml(Long goodsId) {
        Configuration configuration = freeMarkerConfig.getConfiguration();
        try {
            Template template = configuration.getTemplate("item.ftl");

            Map dataModel = new HashMap();

            //商品主表
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            //商品desc表
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            //查询分类名称
            String category1Name = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String category2Name = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String category3Name = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

            //查询sku列表
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");
            example.setOrderByClause("is_default desc");//按照默认降序，使第一个为默认商品
            List<TbItem> itemList = itemMapper.selectByExample(example);


            dataModel.put("itemList",itemList);
            dataModel.put("category1Name",category1Name);
            dataModel.put("category2Name",category2Name);
            dataModel.put("category3Name",category3Name);
            dataModel.put("goods",goods);
            dataModel.put("goodsDesc",goodsDesc);

            //Writer writer = new FileWriter(pagedir+goodsId+".html");
            Writer writer = new PrintWriter(pagedir+goodsId+".html","utf-8");

            template.process(dataModel,writer);

            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {
        try {
            for(Long goodsId:goodsIds){
                new File(pagedir+goodsId+".html").delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
