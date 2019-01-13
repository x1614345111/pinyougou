package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    //定义搜索方法
    public Map<String,Object> search(Map searchMap);


    //保存到索引库
    public void importList(List list);

    //从索引库删除
    public void deleteByGoodsIds(List goodsIds);
}
