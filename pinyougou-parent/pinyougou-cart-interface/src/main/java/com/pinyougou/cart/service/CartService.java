package com.pinyougou.cart.service;

import com.pinyougou.vo.CartVo;

import java.util.List;

public interface CartService {


    //向购物车添加商品
    public List<CartVo> addGoodsToCartList(List<CartVo> list,Long itemId,Integer num);


    /**
     * 从redis中取数据
     * @param username
     * @return
     */
    public List<CartVo> findCartListFromRedis(String username);

    /**
     * 向redis中存数据
     * @param username
     * @param cartVoList
     */
    public void saveCartListToRedis(String username,List<CartVo> cartVoList);

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<CartVo> mergeCartList(List<CartVo> cartList1,List<CartVo> cartList2);
}
