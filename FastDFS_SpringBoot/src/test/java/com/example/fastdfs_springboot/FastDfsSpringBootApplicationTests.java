package com.example.fastdfs_springboot;

import com.luhuiguo.fastdfs.domain.StorePath;
import com.luhuiguo.fastdfs.service.FastFileStorageClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@SpringBootTest
class FastDfsSpringBootApplicationTests {

    @Autowired
    private FastFileStorageClient storageClient;
    @Test
    void contextLoads() throws FileNotFoundException {
        File file = new File("c://1.jpg");
        StorePath path = storageClient.uploadFile(null, new FileInputStream(file),
                file.length(), file.getName());
        System.out.println(path.getFullPath());
    }

    @Test
    void downLoads() throws IOException {
        byte[] bytes = storageClient.downloadFile("group1", "M00/00/00/wKiWWGY19YWAQkytAAQbmPpB2Xg9.1.jpg");
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        BufferedImage image = ImageIO.read(bis);
        bis.close();
        File file = new File("d://output.jpg");
        ImageIO.write(image,"jpg",file);
    }

}
