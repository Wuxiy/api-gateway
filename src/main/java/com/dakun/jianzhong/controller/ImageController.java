package com.dakun.jianzhong.controller;

import com.dakun.jianzhong.service.qiniu.QiniuConstant;
import com.dakun.jianzhong.service.qiniu.QiniuFile;
import com.dakun.jianzhong.utils.MD5;
import com.dakun.jianzhong.utils.Result;
import com.dakun.jianzhong.utils.ResultGenerator;
import com.qiniu.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by wangh09 on 2017/7/24.
 */
@RestController
@RequestMapping("/images")
public class ImageController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/upload-token")
    public Result detail() {
        StringMap putPolicy = new StringMap().putNotEmpty("returnBody",
                "{\"key\": $(key),\"ext\":$(ext)}");
        String token = QiniuFile.getuploadtoken(QiniuConstant.bucket_resources, putPolicy);
        return ResultGenerator.genSuccessResult(token);
    }
    //获取评论图片上传token
//    @RequestMapping("/getCommentToken")
//    public Result getToken(@RequestParam String key){
//        Map<String, Object> result = new HashMap<String, Object>();
//        if (key == null) {
//            return ResultGenerator.genFailResult("parameter error");
//        }
//        String fileName = "product/spec/comment";
//        Date date = new Date();
//        String localtime = System.currentTimeMillis() + "";
//        fileName += MD5.getMD5String(localtime + key);
//        result.put("key", fileName);
//        StringMap putPolicy = new StringMap()
//                .putNotEmpty("returnBody",
//                        "{\"key\": $(key),\"ext\":$(ext),\"exif\":$(exif)}");
//        result.put("token", QiniuFile.getuploadtoken(QiniuConstant.bucket_product, putPolicy));
//        return ResultGenerator.genSuccessResult(result);
//    }
    //获取视频的地址
    @GetMapping("/getvideourl")
    public Result getvideourl(@RequestParam(required = true) String key) {
            String domain = QiniuConstant.Domain_articleresource;
            String baseUrl = QiniuFile.getPublishUrl(domain,key);
            return ResultGenerator.genSuccessResult(baseUrl);
    }
        //用于网页获取小图片地址
    @GetMapping("/getspicurl")
    public Result getpicurl(@RequestParam(required = true) String bucket, @RequestParam(required = true) String key) {
        try {
            String domain = "";
            switch (bucket) {
                case QiniuConstant.bucket_account:
                    domain = QiniuConstant.Domain_account;
                    break;
                case QiniuConstant.bucket_product:
                    domain = QiniuConstant.Domain_product;
                    break;
                case QiniuConstant.bucket_social:
                    domain = QiniuConstant.Domain_social;
                    break;
                case QiniuConstant.bucket_resources:
                    domain = QiniuConstant.Domain_resources;
                    break;
                case QiniuConstant.bucket_articleresource:
                    domain = QiniuConstant.Domain_articleresource;
                    break;
                default:
                    return ResultGenerator.genFailResult("bucket未找到");
            }

            return ResultGenerator.genSuccessResult(QiniuFile.getdownloadurl(domain, key,
                    "?imageView2/2/h/200", QiniuConstant.portrait_download_webpage_exp));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult(e.getCause().getMessage());
        }
    }

    //获取上传token
    //bucket:account,resources,social,product,articleresource
    @RequestMapping(value = "/getuploadtoken", method = RequestMethod.GET)
    public Result uploadprepare(@RequestParam(value = "key") String key,
                                @RequestParam(value = "bucket") String bucket,
                                @RequestParam(value = "type") Integer type) {

        if (key == null || bucket == null || type == null) {
            return ResultGenerator.genFailResult("parameter error");
        }

        Object rs = null;
        if (key.contains(";")) {
            List list = new ArrayList<Map<String, Object>>();
            for (String skey : key.split(";")) {
                list.add(genToken(skey, bucket, type));
            }
            rs = list;
        } else {
            rs = genToken(key, bucket, type);
        }
        return ResultGenerator.genSuccessResult(rs);
    }

    //获取视频上传token
    //bucket:account,resources,social,product,articleresource
    @RequestMapping(value = "/getuploadtoken4V", method = RequestMethod.GET)
    public Result uploadprepare4V(@RequestParam(value = "key") String key,
                                @RequestParam(value = "bucket") String bucket) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (key == null || bucket == null ) {
            return ResultGenerator.genFailResult("parameter error");
        }

        String suffix = key.substring(key.lastIndexOf("."));
        String fileName = "spec/video/";
        String localtime = System.currentTimeMillis() + "";
        fileName += MD5.getMD5String(localtime + key);
        fileName += suffix;
        result.put("key", fileName);
        StringMap putPolicy = new StringMap()
                .putNotEmpty("returnBody",
                        "{\"key\": $(key),\"ext\":$(ext),\"exif\":$(exif)}")
                .putNotEmpty("mimeLimit", "video/mp4");
        result.put("token", QiniuFile.getuploadtoken(bucket, putPolicy));
        return ResultGenerator.genSuccessResult(result);
    }

    private Map<String, Object> genToken(String key, String bucket, Integer type) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String md5 = "";
            String fileName = "";
            Date date = new Date();
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");// 小写的mm表示的是分钟
            String localtime = System.currentTimeMillis() + "";



            switch (type) {
                //二维码溯源图片上传
                case 46:
                    fileName = "product/speccomment/";
                    break;
                //用药图文图片上传
                case 47:
                    fileName = "product/specarticle/";
                    break;
                default:
                    fileName = QiniuConstant.pictureMap.get(type);
                    if(fileName==null){
                        return result;
                    }
                    break;
            }
            fileName += MD5.getMD5String(localtime + key);
            result.put("key", fileName);
            StringMap putPolicy = new StringMap()
                    .putNotEmpty("returnBody",
                            "{\"key\": $(key),\"ext\":$(ext),\"exif\":$(exif)}");
            putPolicy.putNotEmpty("persistentOps",
                    "imageMogr2/thumbnail/800x800");//限定长边，生成不超过 800x800 的缩略图
            result.put("token", QiniuFile.getuploadtoken(bucket, putPolicy));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //获取上传token
    //bucket:account,resources,social,product,articleresource
    @RequestMapping(value = "/getcommontoken", method = RequestMethod.GET)
    public Result uploadpdf(@RequestParam(value = "key") String key,
                                @RequestParam(value = "bucket") String bucket,
                                @RequestParam(value = "type") Integer type) {

        if (key == null || bucket == null || type == null) {
            return ResultGenerator.genFailResult("parameter error");
        }
        Object rs = null;
        if (key.contains(";")) {
            List list = new ArrayList<Map<String, Object>>();
            for (String skey : key.split(";")) {
                list.add(getcommontoken(skey, bucket, type));
            }
            rs = list;
        } else {
            rs = getcommontoken(key, bucket, type);
        }
        return ResultGenerator.genSuccessResult(rs);
    }
    private Map<String, Object> getcommontoken(String key, String bucket, Integer type) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String md5 = "";
            String fileName = "";
            Date date = new Date();
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");// 小写的mm表示的是分钟
            String localtime = System.currentTimeMillis() + "";
            switch (type) {
                /************作物部分*****************/
                /**
                 *42:许可证 43：批准文件
                 *44：标准
                 */
                case 42:
                    fileName = "product/license/";
                    break;
                case 43:
                    fileName = "product/approve/";
                    break;
                case 44:
                    fileName = "product/standard/";
                    break;

                default:
                    return result;
            }
            fileName += MD5.getMD5String(localtime + key);
            result.put("key", fileName);
            StringMap putPolicy = new StringMap()
                    .putNotEmpty("returnBody",
                            "{\"key\": $(key),\"ext\":$(ext),\"exif\":$(exif)}");
            result.put("token", QiniuFile.getuploadtoken(bucket, putPolicy));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //admin前端ueditor组件获取配置信息
    @RequestMapping(value = "/controller.action", method = RequestMethod.GET)
    public Result controller(@RequestParam(value = "action") String action) {
        String imageUrl = "http://up-z1.qiniu.com/";
        Map<String, Object> result = new HashMap<String, Object>();
        switch (action) {
            case "config":
                String[] strings = {".jpg", ".png", ".jpeg", ".gif", ".bmp"};
                List<String> imgTypeList = Arrays.asList(strings);
                result.put("imageUrl", imageUrl);
                result.put("imageActionName", "uploadimage");
                result.put("imageFieldName", "file");
                result.put("imageMaxSize", "2048000");
                result.put("imageAllowFiles", imgTypeList);
                result.put("imageUrlPrefix", "http://otpbyg9fz.bkt.clouddn.com/");
                break;
            case "uploadimage":
                result.put("imageUrl",imageUrl);
                break;

        }
        return ResultGenerator.genSuccessResult(result);
    }

}
