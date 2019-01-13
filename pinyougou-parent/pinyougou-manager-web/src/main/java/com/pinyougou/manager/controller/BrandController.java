package com.pinyougou.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService service;

    /**
     * 查询所有
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<TbBrand> findAll(){
       return service.findAll();
    }

    /**
     * 分页
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("/findPage.do")
    public PageResult findPage(@RequestParam(name = "page",defaultValue = "1") int page,@RequestParam(name = "size",defaultValue = "10") int size){
        return service.findPage(page,size);
    }

    /**
     * 保存
     * @param brand
     * @return
     */
    @RequestMapping("/save.do")
    public Result save(@RequestBody TbBrand brand){

        try {
            service.add(brand);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }

    /**
     * 根据id查询一个，修改的回显功能
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public TbBrand findOne(long id){
        return service.findOne(id);
    }

    /**
     * 修改
     * @param brand
     * @return
     */
    @RequestMapping("/update.do")
    public Result update(@RequestBody TbBrand brand){
        try {
           service.update(brand);
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }

    /**
     * 批量删除
     * @return
     */
    @RequestMapping("/delete.do")
    public Result delete(long[] ids){
        try {
            service.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    /**
     * 条件查询
     * @param brand
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(@RequestBody TbBrand brand,@RequestParam(name = "page",defaultValue = "1") int page,@RequestParam(name = "size",defaultValue = "10") int size){
        return service.findPage(brand,page,size);
    }

    /**
     * 查询类型模板下拉菜单 品牌列表
     * @return
     */
    @RequestMapping("/selectOptionList.do")
    public List<Map> selectOptionList(){
        return service.selectOptionList();
    }
}
