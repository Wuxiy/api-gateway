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

    private Map<String, Object> genToken(String key, String bucket, Integer type) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String md5 = "";
            String fileName = "";
            Date date = new Date();
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");// 小写的mm表示的是分钟
            String localtime = System.currentTimeMillis() + "";
            switch (type) {
                /**************基本信息部分**************/
                //0:头像
                case 0:
                    fileName = "user/portrait/";
                    break;

                /**************专家部分**************/
                //1：身份证正面；2：身份证背面；3：证件
                case 1:
                    fileName = "expert/idfront/";
                    break;
                case 2:
                    fileName = "expert/idback/";
                    break;
                case 3:
                    fileName = "expert/credential/";
                    break;

                /**************经销商部分**************/
                //4：身份证正面；5：身份证背面；6：营业执照；7：店铺照片
                case 4:
                    fileName = "dealer/idfront/";
                    break;
                case 5:
                    fileName = "dealer/idback/";
                    break;
                case 6:
                    fileName = "dealer/lisense/";
                    break;
                case 7:
                    fileName = "dealer/shopphoto/";
                    break;

                /************作物部分*****************/
                /**
                 *30:作物图片,31:作物种类图片,32:虫害图片,33：虫害虫态图片
                 *34：病害图片,35：缺素症图片，36：生物协迫图片
                 * 37：草害图片
                 */
                case 30:
                    fileName = "crop/picture/";
                    break;
                case 31:
                    fileName = "crop/catalog/picture/";
                    break;
                case 32:
                    fileName = "pest/harmpic/";
                    break;
                case 33:
                    fileName = "pest/pestpic/";
                case 34:
                    fileName = "crop/disease/";
                    break;
                case 35:
                    fileName = "crop/deficiency/";
                    break;
                case 36:
                    fileName = "crop/threat/";
                    break;
                case 37:
                    fileName = "crop/grass/";
                    break;
                case 40:
                    fileName = "product/product/";
                    break;
                case 41:
                    fileName = "product/trademark/";
                    break;
                case 45:
                    fileName = "product/specification/";
                    break;
                /*************文章部门********/
                // 8：文章主图片
                case 8:
                    fileName = "article/mainpic/";
                    break;
                case 9:
                    fileName = "question/detail-pic/";
                    break;
                default:
                    return result;
            }
            fileName += MD5.getMD5String(localtime + key);
            result.put("key", fileName);
            StringMap putPolicy = new StringMap()
                    .putNotEmpty("returnBody",
                            "{\"key\": $(key),\"ext\":$(ext),\"exif\":$(exif)}");
            putPolicy.putNotEmpty("persistentOps",
                    "imageMogr2/thumbnail/800*800");
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
                result.put("imageUrl", imageUrl);
                break;

        }
        return ResultGenerator.genSuccessResult(result);
    }

}
