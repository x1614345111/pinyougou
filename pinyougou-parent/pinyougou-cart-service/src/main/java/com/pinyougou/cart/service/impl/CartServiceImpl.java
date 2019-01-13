package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<CartVo> addGoodsToCartList(List<CartVo> cartList, Long itemId, Integer num) {
        //1 根据itemId 查询sku对象
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //2  根据sku对象得到商家id
        String sellerId = item.getSellerId();
        //3  根据商家id 查询购物车中是否存在该商家
        CartVo cartVo = selectCartListBySellerId(cartList, sellerId);


        //4 如果不存在，创建新的对象
        if (cartVo == null) {
            cartVo = new CartVo();
            cartVo.setSellerId(sellerId);
            cartVo.setSellerName(item.getSeller());

            TbOrderItem orderItem = createOrderItem(item, num);

            List<TbOrderItem> orderItemList = new ArrayList<>();
            orderItemList.add(orderItem);
            cartVo.setOrderItemList(orderItemList);
            // 4.1 将新建的购物车对象加入到 购物车列表
            cartList.add(cartVo);
        } else {
            //5 如果存在 查询购物车明细是否存在该商品
            TbOrderItem orderItem = searchOrderItemByItemId(cartVo.getOrderItemList(), itemId);
            //5.1 如果不存在，创建新的商品明细
            if (orderItem == null) {
                orderItem = createOrderItem(item, num);
                cartVo.getOrderItemList().add(orderItem);
            } else {
                //5.2  如果存在，增加数量，修改金额
                int newNum =(orderItem.getNum() + num);
                if(newNum<1){
                    newNum=1;
                }
                orderItem.setNum(newNum);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));

                //如果数量操作后小于等于0，则删除
                if (orderItem.getNum() <= 0) {
                    cartVo.getOrderItemList().remove(orderItem);
                }

                if (cartVo.getOrderItemList().size() == 0) {
                    cartList.remove(cartVo);
                }

            }
        }
        return cartList;
    }


    //判断当前购物车列表 是否存在该商家
    private CartVo selectCartListBySellerId(List<CartVo> cartList, String sellerId) {

        for (CartVo cartVo : cartList) {
            if (cartVo.getSellerId().equals(sellerId)) {
                return cartVo;
            }
        }
        return null;
    }


    /**
     * 添加商品明细
     *
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if (num <= 0) {
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }


    /**
     * 判断商品明细中有没有该sku
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }


    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 从redis中取出数据
     * @param username
     * @return
     */
    @Override
    public List<CartVo> findCartListFromRedis(String username) {
        System.out.println("cong redis quququququ");
        List<CartVo> cartList = (List<CartVo>) redisTemplate.boundHashOps("cartList").get(username);
        if(cartList==null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 向redis中添加数据
     * @param username
     * @param cartVoList
     */
    @Override
    public void saveCartListToRedis(String username, List<CartVo> cartVoList) {
        System.out.println("xiang redis ++++++++++++");
        redisTemplate.boundHashOps("cartList").put(username,cartVoList);
    }


    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<CartVo> mergeCartList(List<CartVo> cartList1,List<CartVo> cartList2){

        System.out.println("he bing gou wu che !!!!!!!!!!!");
        for (CartVo cartVo : cartList2) {

            for (TbOrderItem orderItem : cartVo.getOrderItemList()) {

                cartList1 = addGoodsToCartList(cartList1, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList1;
    }
}

