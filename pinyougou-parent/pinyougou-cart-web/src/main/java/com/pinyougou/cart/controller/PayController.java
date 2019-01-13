package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;


    @RequestMapping("/createNative")
    public Map createNative(){

        IdWorker idWorker = new IdWorker();
       return weixinPayService.createNative(idWorker.nextId()+"","1");
    }


    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){

        Result result = null;

        int x= 0;
        while (true){
            Map map = weixinPayService.queryPayStatus(out_trade_no);
            if(map==null){
                result = new Result(false,"支付失败");
                break;
            }

            if(map.get("trade_state").equals("SUCCESS")){//如果支付成功
                result = new Result(true,"支付成功");
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            x++;
            if(x>=5){
                result =   new Result(false,"二维码超时");
                break;
            }
            System.out.println(x);
        }
        return result;
    }
}
