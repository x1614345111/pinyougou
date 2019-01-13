package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.GoodsVo;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
	 * 增加
	 * @param goodsVo
	 * @return
	 */
	@RequestMapping("/add.do")
	public Result add(@RequestBody GoodsVo goodsVo){
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		goodsVo.getGoods().setSellerId(sellerId);
		try {
			goodsService.add(goodsVo);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goodsVo
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody GoodsVo goodsVo){

		//只允许操作当前商家自己的商品
		GoodsVo goodsVo2 = goodsService.findOne(goodsVo.getGoods().getId());
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		if(!goodsVo.getGoods().getSellerId().equals(sellerId)||!goodsVo2.getGoods().getSellerId().equals(sellerId)){
			return new Result(false,"非法操作！");
		}
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
		String sellerName = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(sellerName);
		return goodsService.findPage(goods, page, rows);		
	}
	
}
