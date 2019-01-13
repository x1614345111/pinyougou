package com.pinyougou.vo;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

public class GoodsVo implements Serializable {

    private TbGoods Goods;
    private TbGoodsDesc GoodsDesc;
    private List<TbItem> itemList;

    public TbGoods getGoods() {
        return Goods;
    }

    public void setGoods(TbGoods goods) {
        Goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return GoodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        GoodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
