package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.vo.CartVo;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 5000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/findCartList")
    public List<CartVo> findCartList(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "utf-8");

        if(cartListString==null || "".equals(cartListString)){
            cartListString="[]";
        }
        List<CartVo> cartList_cookie = JSON.parseArray(cartListString,CartVo.class);


        if("anonymousUser".equals(username)){//未登录，从cookie中取
            System.out.println("cong cookie zhong qu");
            return cartList_cookie;
        }else {//已经登录，从redis中取
            List<CartVo> cartList_redis = cartService.findCartListFromRedis(username);
            if(cartList_cookie.size()>0){
                //如果 cookie中有数据，合并购物车
               cartList_redis= cartService.mergeCartList(cartList_redis,cartList_cookie);
            }
            // 合并后 从cookie中删除数据
            CookieUtil.deleteCookie(request,response,"cartList");
            //将合并后的购物车，添加到redis中
            cartService.saveCartListToRedis(username,cartList_redis);

            return cartList_redis;
        }
    }


    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId,Integer num){

        //设置允许哪个域可以跨域访问
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        //设置 可以跨域操作cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");


        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CartVo> cartList = findCartList();
        cartList = cartService.addGoodsToCartList(cartList, itemId, num);
        try {
            if("anonymousUser".equals(username)){//未登录，向cookie中添加数据
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"utf-8");
                System.out.println("xiang  cookie +++++++++++++++++");
            }else {//已经登录，向redis中添加数据
                cartService.saveCartListToRedis(username,cartList);
            }
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
}
