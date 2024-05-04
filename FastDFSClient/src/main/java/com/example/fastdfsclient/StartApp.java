package com.example.fastdfsclient;

import com.example.fastdfsclient.config.FastDFSClient;
import org.csource.common.NameValuePair;

import java.io.*;
import java.util.Arrays;

/**
 * ClassName: StartApp
 * Package: com.example.fastdfsclient
 * Description:
 *
 * @Author R
 * @Create 2024/5/4 15:56
 * @Version 1.0
 */
public class StartApp {
    public static void main(String[] args) {
        //fileUploads();
        //downLoad();
        getFileInfo();
    }

    public static void fileUpload() {
        File file = new File("c:\\1.jpg");
        String[] strings = FastDFSClient.uploadFile(file, file.getName());
        System.out.println(strings.toString());
        System.out.println("上传成功"+ Arrays.asList(strings));

        //上传成功[group1, M00/00/00/wKiWWGY160OAG1XEAAQbmPpB2Xg566.jpg]


    }

    //改进上传方法 可以得到元数据
    public static void fileUploads() {
        File file = new File("c:\\1.jpg");
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            String[] strings = FastDFSClient.uploadFile(fileInputStream, file.getName());
            System.out.println(strings.toString());
            System.out.println("上传成功"+ Arrays.asList(strings));

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        //上传成功[group1, M00/00/00/wKiWWGY160OAG1XEAAQbmPpB2Xg566.jpg]


    }

    public static void downLoad() {
        InputStream is = FastDFSClient
                .downloadFile("group1", "M00/00/00/wKiWWGY160OAG1XEAAQbmPpB2Xg566.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File("D://12.jpg"));
            int index = 0;
            while ((index = is.read())!= -1){
                fos.write(index);
            }
            fos.flush();
            fos.close();
            is.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 获取元素信息
     */
    public static void getFileInfo(){
        NameValuePair[] nameValuePairs = FastDFSClient.getMetaDate("group1", "M00/00/00/wKiWWGY18JWAWzljAAQbmPpB2Xg186.jpg");
        if(nameValuePairs!=null && nameValuePairs.length>0) {
            for (NameValuePair nameValuePair : nameValuePairs) {
                System.out.println(nameValuePair.getName());
                System.out.println(nameValuePair.getValue());
            }
        }
    }

}
