package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {
    /**
     * 查询所有
     * @return
     */
    List<TbBrand> findAll();

    /**
     * 分页查询
     * @param pageNum 当前页码
     * @param pageSize  每页显示数量
     * @return
     */
    PageResult findPage(int pageNum , int pageSize);

    /**
     * 添加
     * @param brand
     */
    void add(TbBrand brand);

    /**
     * 根据id查询一个
     * @param id
     * @return
     */
    TbBrand findOne(Long id);

    /**
     * 修改
     * @param brand
     */
    void update(TbBrand brand);

    /**
     * 删除
     * @param ids
     */
    void delete(long[] ids);

    /**
     * 条件查询+分页
     * @param brand
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult findPage(TbBrand brand,int pageNum , int pageSize);


    List<Map> selectOptionList();
}
