package com.itheima;

import org.csource.fastdfs.*;

public class demo {

    public static void main(String[] args) throws Exception {

        ClientGlobal.init("D:\\2018workSpace\\pinyougouSpace\\day01\\fastDFSdemo\\src\\main\\resources\\fdfs_client.conf");

        TrackerClient trackerClient = new TrackerClient();

        TrackerServer trackerServer = trackerClient.getConnection();

        StorageServer storageServer = null;
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);

        String[] strings = storageClient.upload_file("D:\\ideaimage\\1e03b80792f21501160ea799cb6bc749.jpg", "jpg", null);

        for (String string : strings) {
            System.out.println(string);
        }

    }
}
