package cn.mikulink.sinaimageupload;

import cn.mikulink.sinaimageupload.service.SinaImageUploadService;

/**
 * created by MikuNyanya on 2022/5/17 10:54
 * For the Reisen
 */
public class Main {
    public static void main(String[] a) {
        try {
            String sinaImageUrl = "";
            //网站cookie 使用新浪微博网站cookie即可
            String cookie = "cookie";

            //本地图片上传
            String localFilePath = "E://rabbit.jpg";
//            sinaImageUrl = SinaImageUploadService.uploadByLocalImage(localFilePath,cookie);

            //网络图片上传
            String imageUrl = "https://i0.hdslb.com/bfs/article/fd84d162fedf20cc0daa213e05aa8fd42f57993f.jpg";
            sinaImageUrl = SinaImageUploadService.uploadByImageUrl(imageUrl, cookie);

            //https://tvax1.sinaimg.cn/large/64130597ly1h2bct8u2wyj20qk1b8wkf.jpg
            System.out.println(sinaImageUrl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
