package cn.mikulink.sinaimageupload.service;


import cn.hutool.core.codec.Base64;
import cn.hutool.http.*;
import cn.mikulink.sinaimageupload.entity.ImageUploadResponseInfo;
import cn.mikulink.sinaimageupload.util.FileUtil;
import cn.mikulink.sinaimageupload.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * create by MikuLink on 2022/05/13 16:53
 * for the Reisen
 * 新浪图床
 */
@Getter
@Setter
public class SinaImageUploadService {
    private static final String UPLOAD_URL = "https://picupload.weibo.com/interface/pic_upload.php?ori=1&mime=image%2Fjpeg&data=base64&url=0&markpos=1&logo=&nick=0&marks=1&app=miniblog";
    private static final String SINA_IMAGE_PREFIX = "https://tvax1.sinaimg.cn/large/";
    //返回报文多余信息
    private static final String SP_STR = "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
            "    <script type=\"text/javascript\">document.domain=\"sina.com.cn\";</script> ";

    /**
     * 使用网络图片上传
     *
     * @param imageUrl 网络图片链接
     * @param cookie   微博网站cookie
     */
    public static String uploadByImageUrl(String imageUrl, String cookie) throws IOException {
        URL url = new URL(imageUrl);
        String fileSuffix = FileUtil.getFileSuffix(imageUrl).replace(".", "");
        BufferedImage image = ImageIO.read(url);
        String base64Str = parseBase64(image, fileSuffix);
        return doRequest(base64Str, fileSuffix, cookie);
    }

    /**
     * 使用本地图片
     *
     * @param imagePath 本地图片文件路径
     * @param cookie    微博网站cookie
     */
    public static String uploadByLocalImage(String imagePath, String cookie) throws IOException {
        File imageLocal = new File(imagePath);
        String fileSuffix = FileUtil.getFileSuffix(imagePath).replace(".", "");
        BufferedImage image = ImageIO.read(imageLocal);
        String base64Str = parseBase64(image, fileSuffix);
        return doRequest(base64Str, fileSuffix, cookie);
    }

    private static String parseBase64(BufferedImage image, String fileSuffix) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, fileSuffix, out);
        byte[] imgBytes = out.toByteArray();

        return Base64.encode(imgBytes);
    }

    /**
     * 上传至微博
     * @param base64Image 图片base64
     * @param fileSuffix 图片类型
     * @param cookie 新浪网站cookie
     * @return 图片链接
     */
    private static String doRequest(String base64Image, String fileSuffix, String cookie) {
        if (StringUtil.isEmpty(fileSuffix)) {
            fileSuffix = "jpg";
        }

        JSONObject param = new JSONObject();
        param.put("b64_data", base64Image);

        HttpRequest httpRequest = HttpUtil.createPost(UPLOAD_URL);
        httpRequest.contentType(ContentType.MULTIPART.getValue());
        httpRequest.header("cookie", cookie);

        HttpResponse response = httpRequest.timeout(HttpGlobalConfig.getTimeout()).form(param).execute();
        String body = response.body();

        //记录接口返回日志
        System.out.println("返回报文:" + body);

        String tempBody = body.substring(SP_STR.length());

        ImageUploadResponseInfo rspInfo = JSONObject.parseObject(tempBody, ImageUploadResponseInfo.class);
        return SINA_IMAGE_PREFIX + rspInfo.getData().getPics().getPic1().getPid() + "." + fileSuffix;
    }
}
