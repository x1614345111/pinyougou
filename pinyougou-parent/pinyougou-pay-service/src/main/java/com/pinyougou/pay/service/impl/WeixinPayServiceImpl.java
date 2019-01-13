package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Override
    public Map createNative(String out_trade_no, String total_fee) {

        // 1. 创建参数
        Map param = new HashMap();

        param.put("appid",appid);//公众账号id
        param.put("mch_id",partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body","品优购支付");//支付名字
        param.put("out_trade_no",out_trade_no);//订单号
        param.put("total_fee",total_fee);//金额
        param.put("spbill_create_ip","127.0.0.1");//终端ip
        param.put("notify_url","https://www.baidu.com");//回调地址
        param.put("trade_type","NATIVE");//交易类型

        try {
            //把map转换为xml字符串
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);

            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");//连接 微信接口
            client.setHttps(true);//不使用https，默认false
            client.setXmlParam(xmlParam);
            client.post();

            String result = client.getContent();//获得XML字符串结果
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);//将返回结果转换为map


            Map map = new HashMap();//返回结果resultMap有很多数据，选择需要返回的，添加到map返回

            map.put("code_url",resultMap.get("code_url"));//支付url
            map.put("out_trade_no",out_trade_no);//订单号
            map.put("total_fee",total_fee);//支付金额

            return  map;

        } catch (Exception e) {
            e.printStackTrace();

            return new HashMap();
        }

    }

    /**
     * 查询订单状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {

        Map param = new HashMap();

        param.put("appid",appid);
        param.put("mch_id",partner);
        param.put("out_trade_no",out_trade_no);
        param.put("nonce_str",WXPayUtil.generateNonceStr());

        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);

            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();

            String result = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            System.out.println(resultMap);
            return  resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
