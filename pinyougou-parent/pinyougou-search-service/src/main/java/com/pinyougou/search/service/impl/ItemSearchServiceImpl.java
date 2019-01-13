package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 主查询线路
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map searchMap) {
        Map map = new HashMap();
        Query query = new SimpleQuery("*:*");
        if (searchMap.get("keywords") != null && !"".equals(searchMap.get("keywords"))) {
            //关键字空格处理
            String keywords = (String) searchMap.get("keywords");
            searchMap.put("keywords",keywords.replace(" ",""));

            map.putAll(searchList(searchMap));
            List categoryList = searchCategoryList(searchMap);
            map.put("categoryList",categoryList);

            String categoryName = (String) searchMap.get("category");
            if(!"".equals(categoryName)){
                map.putAll(searchBrandAndSpecList(categoryName));
            }else {
                if(categoryList.size()>0){
                    map.putAll(searchBrandAndSpecList(categoryList.get(0).toString()));
                }
            }

        } else {
            ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
            map.put("rows", scoredPage.getContent());
        }
        return map;
    }

    //设置高亮
    private Map searchList(Map searchMap) {
        Map map = new HashMap();
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮的域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
        highlightOptions.setSimplePostfix("</em>");//高亮后缀
        query.setHighlightOptions(highlightOptions);//设置高亮选项
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //按照商品分类过滤
        if(!"".equals(searchMap.get("category"))){
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //按照品牌过滤
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //按照规格过滤
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //按照价格区间过滤
        if(!"".equals(searchMap.get("price"))){
            String priceStr = (String) searchMap.get("price");
            String[] price = priceStr.split("-");
            if(!price[0].equals("0")){//如果起点不为0
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if(!price[1].equals("*")){//如果终点不为*
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //排序
        String sortValue = (String) searchMap.get("sort");
        String sortFieldValue = (String) searchMap.get("sortField");

        if(sortValue!=null && !"".equals(sortValue)){
            if(sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortFieldValue);
                query.addSort(sort);
            }
            if(sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortFieldValue);
                query.addSort(sort);
            }
        }

        //分页查询
        Integer pageNo= (Integer) searchMap.get("pageNo");//提取页码
        if(pageNo==null){
            pageNo=1;//默认第一页
        }
        Integer pageSize=(Integer) searchMap.get("pageSize");//每页记录数
        if(pageSize==null){
            pageSize=20;//默认20
        }
        query.setOffset((pageNo-1)*pageSize);//从第几条记录查询
        query.setRows(pageSize);


        //  获取结果集
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        for (HighlightEntry<TbItem> h : page.getHighlighted()) {//循环高亮入口集合
            TbItem item = h.getEntity();//获取原实体类
            if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
            }
        }


        map.put("rows", page.getContent());
        map.put("totalPages", page.getTotalPages());//返回总页数
        map.put("total", page.getTotalElements());//返回总记录数
        return map;
    }


    /**
     * 使用分组，查询分类列表
     *
     * @param searchMap
     * @return
     */
    private List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList();
        Query query = new SimpleQuery();
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
        }
        return list;
    }

    @Autowired
    private RedisTemplate redisTemplate;



    /**
     * 从缓存中取出品牌列表，规格列表
     * @param category
     * @return
     */
    private Map searchBrandAndSpecList(String category){
        Map map = new HashMap();

        //根据分类名称从缓存取出模板id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if(typeId!=null){
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList",brandList);

            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);
        }
        return map;
    }



    /**
     * 更新保存索引库
     * @param list
     */
    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 从索引库删除
     * @param goodsIds
     */
    @Override
    public void deleteByGoodsIds(List goodsIds) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
        query.addCriteria(criteria);

        solrTemplate.delete(query);
        solrTemplate.commit();
    }

}
