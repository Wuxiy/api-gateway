package com.dakun.jianzhong.service.qiniu;

import com.dakun.jianzhong.utils.TextUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.util.Base64;
import com.qiniu.util.StringMap;
import com.qiniu.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;

/**
 * Created by lichenghai on 2015/9/23.
 */
public class QiniuUploadTest {

    @Resource
    private static RestTemplate restTemplate;
    public static void main(String[] args) throws QiniuException {
        System.out.println(TextUtils.passwdEncodeToDB("t4est5"));

        //uploadtest();
        //uploadfile();
        //getdownloadurl();


        //移动文件
        //QiniuFile.move("portrait", "transfile",	"portrait", "000.jpg");

        //重命名文件
        //QiniuFile.rename("portrait", "transfile", "test01071");

        //删除文件
        //QiniuFile.delete("portrait", "000.jpg");
       // ArrayList<Integer> idList = new ArrayList<>();

/*        String detailLink = "/detail/deficiency/649";
        RestTemplate restTemplates = new RestTemplate();
        HttpEntity<Result>  response_socialTag = restTemplates.getForEntity("http://192.168.50.104:2911/remove-me/social-service/tag/deleteByDetailLink?detailLink="+detailLink,Result.class);
        Result socialTag_result = response_socialTag.getBody();
        if (socialTag_result.getStatus() == ResultCode.SUCCESS.status) {
            System.out.println(socialTag_result.getMessage());
        } else {
            System.out.println("数据已删除，删除TAG信息失败！");
        }*/

        getUploadToken();
    }


    //获取下载地址测试
    public static void getdownloadurl(){
        String downloadurl = QiniuFile.getdownloadurl(QiniuConstant.Domain_account,"transfile","",3600);
        System.out.println(downloadurl);
    }

    //获取下载地址测试--带缩略图功能
    public static void getdownloadurl2(){
        String downloadurl = QiniuFile.getdownloadurl(QiniuConstant.Domain_account,"transfile3","?imageView2/2/h/350/w/350",3600);
        System.out.println(downloadurl);
    }


    //上传文件测试：服务器直传
    public static void uploadfile(){

        //System.out.println(ACCESS_KEY);

        File file = new File("D:\\work\\nongyequan\\picture\\asfwg.jpg");
        System.out.println("==strat upload ==");
        StringMap putPolicy = new
                StringMap().putNotEmpty("returnBody",
                "{\"key\": $(key),\"ext\":$(ext)}");
        try {
            Response res = QiniuFile.upload("attence",putPolicy,file,"testfile");
            if(200==res.statusCode) {
                System.out.println("upload success!");
            }else {
                System.out.println("upload failed!\n" + res.bodyString());
            }
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    public static String getUploadToken() {

        StringMap putPolicy = new StringMap()
                .putNotEmpty("returnBody",
                        "{\"key\": $(key),\"ext\":$(ext),\"exif\":$(exif)}");
        putPolicy.putNotEmpty("persistentOps",
                "imageMogr2/thumbnail/800*800");

        String token = QiniuFile.getuploadtoken(QiniuConstant.bucket_articleresource, putPolicy);
        System.out.println(token);

        return token;
    }

    //上传文件测试：返回uptoken
    public static String uploadfile2(){
        StringMap putPolicy = new
                StringMap()
                //上传成功后返回值
                .putNotEmpty("returnBody",
                        "{\"key\": $(key),\"ext\":$(ext)}")

                //==========上传成功后回调地址=========
                .putNotEmpty("persistentNotifyUrl", "http://zhangsan.com/uploadfinish")

                //==========业务处理==============
                .putNotEmpty(
                        "persistentOps",
                        //=======转码为MP4=========
                        "avthumb/mp4/vcodec/libx264"
                                + "|saveas/"
                                + Base64.encodeToString(StringUtils
                                        .utf8Bytes("newmp4"),
                                Base64.URL_SAFE
                                        | Base64.NO_WRAP)
                                //=========截图==========
                                + ";vframe/jpg/offset/1/w/200/h/200|saveas/"
                                + Base64.encodeToString(
                                StringUtils.utf8Bytes("screenshot"),
                                Base64.URL_SAFE
                                        | Base64.NO_WRAP));
        String upToken = QiniuFile.getuploadtoken(QiniuConstant.bucket_account,putPolicy);
        System.out.println("upToken:"+upToken);
        return "";
    }

    public static void uploadtest(){
        System.out.println(QiniuFile.getdownloadurl(QiniuConstant.Domain_account, "820170726115130", "?imageView2/2/h/200", QiniuConstant.portrait_download_webpage_exp));
    }
}
