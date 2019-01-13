package com.pinyougou.manager.controller;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	/*@Reference
	private ItemSearchService searchService;*/

	@Autowired
	private Destination queueSolrDestination;//用于发送solr导入的消息

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Destination queueSolrDeleteDestination;//用户在索引库中删除记录

	@Autowired
	private Destination topicPageDestination;

	@Autowired
	private Destination topicPageDeleteDestination;//用于删除静态网页的消息

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}

	/**
	 * 修改
	 * @param goodsVo
	 *
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody GoodsVo goodsVo){
		try {
			goodsService.update(goodsVo);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public GoodsVo findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			//searchService.deleteByGoodsIds(Arrays.asList(ids));

			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});

			//删除页面
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});

			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}


	/**
	 * 运营商商品审核，修改商品审核状态
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status){
		try {
			goodsService.updateStatus(ids,status);

			//更新索引库
			if(status.equals("1")){
				List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, status);

				if(itemList.size()>0){
					//searchService.importList(itemList);
					String jsonString = JSON.toJSONString(itemList);
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(jsonString);
						}
					});



				}else {
					System.out.println("更新索引库 错误/无数据");
				}

				/*for (Long id : ids) {
					//itemPageService.genItemHtml(id);
				}*/
				//生成静态页面
				jmsTemplate.send(topicPageDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(ids);
					}
				});
			}
			return new Result(true,"成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"失败");
		}
	}

}
