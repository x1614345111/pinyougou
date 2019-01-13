package com.pinyougou.manager.controller;


import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;



    @RequestMapping("/upload")
    public Result upload(MultipartFile file) throws Exception {
        try {
            String fileName=file.getOriginalFilename();
            String exName=fileName.substring(fileName.lastIndexOf(".")+1);
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            String path = client.uploadFile(file.getBytes(), exName);
            String url= FILE_SERVER_URL+path;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传图片失败");
        }
    }
}
