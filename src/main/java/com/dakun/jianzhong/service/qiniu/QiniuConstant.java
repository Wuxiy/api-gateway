package com.dakun.jianzhong.service.qiniu;

import java.util.HashMap;
import java.util.Map;

public class QiniuConstant{

    public static final String bucket_account = "account";
    public static final String Domain_account = "account.qiniu.doctornong.com";

    public static final String bucket_social = "social";
    public static final String Domain_social = "social.qiniu.doctornong.com";

    public static final String bucket_product = "product";
    public static final String Domain_product = "product.qiniu.doctornong.com";

    public static final String bucket_resources = "resources";
    public static final String Domain_resources = "resources.qiniu.doctornong.com";

    public static final String bucket_articleresource = "articleresource";
    public static final String Domain_articleresource = "articleresource.qiniu.doctornong.com";

	//portrait空间资源下载有效时长,for app
	public static final int portrait_download_app_exp = 3600;

	//portrait空间资源下载有效时长,for web
	public static final int portrait_download_webpage_exp = 1200;

	public static final Map<Integer,String> pictureMap = new HashMap<>();

	static {
        /**************基本信息部分**************/
        pictureMap.put(0,"user/portrait/");
        /**************专家部分**************/
        //1：身份证正面；2：身份证背面；3：证件
        pictureMap.put(1,"expert/idfront/");
        pictureMap.put(2,"expert/idback/");
        pictureMap.put(3,"expert/credential/");

        /**************经销商部分**************/
        //4：身份证正面；5：身份证背面；6：营业执照；7：店铺照片
        pictureMap.put(4,"dealer/idfront/");
        pictureMap.put(5,"dealer/idback/");
        pictureMap.put(6,"dealer/lisense/");
        pictureMap.put(7,"dealer/shopphoto/");

        /************作物部分*****************/
        /**
         *30:作物图片,31:作物种类图片,32:虫害图片,33：虫害虫态图片
         *34：病害图片,35：缺素症图片，36：生物协迫图片
         * 37：草害图片,46:评论图片
         */
        pictureMap.put(30,"crop/picture/");
        pictureMap.put(31,"crop/catalog/picture/");
        pictureMap.put(32,"pest/harmpic/");
        pictureMap.put(33,"pest/pestpic/");
        pictureMap.put(34,"crop/disease/");
        pictureMap.put(35,"crop/deficiency/");
        pictureMap.put(36,"crop/threat/");
        pictureMap.put(37,"crop/grass/");

        pictureMap.put(40,"product/product/");
        pictureMap.put(41,"product/trademark/");
        pictureMap.put(42,"product/license/");
        pictureMap.put(43,"product/approve/");
        pictureMap.put(44,"product/standard/");

        pictureMap.put(45,"product/specification/");
        //需要特殊处理的两个文件路径,web端图片大小的压缩
        //二维码溯源图片上传
        //pictureMap.put(46,"product/speccomment/");
        //用药图文图片上传
        //pictureMap.put(47,"product/specarticle/");

        //农事活动：浇水
        pictureMap.put(50,"activity/water/");
        //农事活动：施肥
        pictureMap.put(51,"activity/fertilize/");
        //农事活动：打药
        pictureMap.put(52,"activity/medicine/");
        //农事活动：自定义活动
        pictureMap.put(53,"activity/custom/");

        /*************文章部门********/
        // 8：文章主图片
        pictureMap.put(8,"article/mainpic/");
        //文章段落图片
        pictureMap.put(80,"article/detail-pic/");
        //文章html文件
        pictureMap.put(81,"article/richtxt/");
        pictureMap.put(9,"question/detail-pic/");
        //示范产品图片
        pictureMap.put(10,"example/product/");
        //使用前图片
        pictureMap.put(101,"example/before/");
        //使用后图片
        pictureMap.put(102,"example/after/");

    }
}
