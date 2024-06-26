package com.example.fastdfsclient.configSafe;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.*;

/**
 * ClassName: FastDFSClient
 * Package: com.example.fastdfsclient.config
 * Description:
 *
 * @Author R
 * @Create 2024/5/4 15:51
 * @Version 1.0
 */

/**
 * 并发安全
 */
public class FastDFSClient {
    private static final String CONF_FILENAME = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "fdfs_client.conf";

    /**
     * 只加载一次.
     */
    static {
        try {
            ClientGlobal.init(CONF_FILENAME);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static StorageClient getStorageClient() {
        StorageClient storageClient = null;
        TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            storageClient = new StorageClient(trackerServer, storageServer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return storageClient;
    }
    /**
     *
     * @param inputStream
     *    上传的文件输入流
     * @param fileName
     *    上传的文件原始名
     * @return
     */
    public static String[] uploadFile(InputStream inputStream, String fileName) {
        try {
            // 文件的元数据
            NameValuePair[] meta_list = new NameValuePair[2];
            // 第一组元数据，文件的原始名称
            meta_list[0] = new NameValuePair("file name", fileName);
            // 第二组元数据
            meta_list[1] = new NameValuePair("file length", inputStream.available()+"");
            // 准备字节数组
            byte[] file_buff = null;
            if (inputStream != null) {
                // 查看文件的长度
                int len = inputStream.available();
                // 创建对应长度的字节数组
                file_buff = new byte[len];
                // 将输入流中的字节内容，读到字节数组中。
                inputStream.read(file_buff);
            }
            // 上传文件。参数含义：要上传的文件的内容（使用字节数组传递），上传的文件的类型（扩展名），元数据
            String[] fileids = getStorageClient().upload_file(file_buff, getFileExt(fileName), meta_list);
            return fileids;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param file
     *            文件
     * @param fileName
     *            文件名
     * @return 返回Null则为失败
     */
    public static String[] uploadFile(File file, String fileName) {
        FileInputStream fis = null;
        try {
            NameValuePair[] meta_list = null; // new NameValuePair[0];
            fis = new FileInputStream(file);
            byte[] file_buff = null;
            if (fis != null) {
                int len = fis.available();
                file_buff = new byte[len];
                fis.read(file_buff);
            }

            String[] fileids = getStorageClient().upload_file(file_buff, getFileExt(fileName), meta_list);
            return fileids;
        } catch (Exception ex) {
            return null;
        }finally{
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据组名和远程文件名来删除一个文件
     *
     * @param groupName
     *            例如 "group1" 如果不指定该值，默认为group1
     * @param remoteFileName
     *            例如"M00/00/00/wKgxgk5HbLvfP86RAAAAChd9X1Y736.jpg"
     * @return 0为成功，非0为失败，具体为错误代码
     */
    public static int deleteFile(String groupName, String remoteFileName) {
        try {
            int result = getStorageClient().delete_file(groupName == null ? "group1" : groupName, remoteFileName);
            return result;
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * 修改一个已经存在的文件
     *
     * @param oldGroupName
     *            旧的组名
     * @param oldFileName
     *            旧的文件名
     * @param file
     *            新文件
     * @param fileName
     *            新文件名
     * @return 返回空则为失败
     */
    public static String[] modifyFile(String oldGroupName, String oldFileName, File file, String fileName) {
        String[] fileids = null;
        try {
            // 先上传
            fileids = uploadFile(file, fileName);
            if (fileids == null) {
                return null;
            }
            // 再删除
            int delResult = deleteFile(oldGroupName, oldFileName);
            if (delResult != 0) {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
        return fileids;
    }

    /**
     * 文件下载
     *
     * @param groupName 卷名
     * @param remoteFileName 文件名
     * @return 返回一个流
     */
    public static InputStream downloadFile(String groupName, String remoteFileName) {
        try {
            byte[] bytes = getStorageClient().download_file(groupName, remoteFileName);
            InputStream inputStream = new ByteArrayInputStream(bytes);
            return inputStream;
        } catch (Exception ex) {
            return null;
        }
    }

    public static NameValuePair[] getMetaDate(String groupName, String remoteFileName){
        try{
            NameValuePair[] nvp = getStorageClient().get_metadata(groupName, remoteFileName);
            return nvp;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 获取文件后缀名（不带点）.
     *
     * @return 如："jpg" or "".
     */
    private static String getFileExt(String fileName) {
        if (StringUtils.isBlank(fileName) || !fileName.contains(".")) {
            return "";
        } else {
            return fileName.substring(fileName.lastIndexOf(".") + 1); // 不带最后的点
        }
    }
}
