package com.pinyougou.task;


import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SeckillTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Scheduled(cron = "0/10 * * * * ?")
    public void refreshSeckillGoods(){
        //System.out.println("执行任务调度"+new Date());
        List ids = new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        criteria.andStockCountGreaterThan(0);
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        criteria.andEndTimeGreaterThan(new Date());
        if(ids.size()>0){
            criteria.andIdNotIn(ids);
        }
        List<TbSeckillGoods> seckillGoodsList=seckillGoodsMapper.selectByExample(example);
        //将数据存入缓存
        for (TbSeckillGoods seckillGoods : seckillGoodsList) {
            redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(),seckillGoods);
        }

        //System.out.println(seckillGoodsList.size()+"条放入缓存");
    }

    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoods(){
        System.out.println("yi chu  miao sha  shang pin ...");

        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();

        //System.out.println(seckillGoodsList);
        for (TbSeckillGoods seckillGoods : seckillGoodsList) {
            if(seckillGoods.getEndTime().getTime()<new Date().getTime()){//如果结束时间小于当前时间，移除
                seckillGoodsMapper.updateByPrimaryKey(seckillGoods);//更新数据库库
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getId());//从缓存中移除

                System.out.println("移除商品：+++++++++++++++++++++"+seckillGoods.getTitle());
            }
        }
    }
}
