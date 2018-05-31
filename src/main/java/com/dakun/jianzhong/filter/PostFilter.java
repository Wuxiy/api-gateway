package com.dakun.jianzhong.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dakun.jianzhong.service.qiniu.QiniuConstant;
import com.dakun.jianzhong.service.qiniu.QiniuFile;
import com.dakun.jianzhong.utils.JWTUtils;
import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;


/**
 * Created by wangh09 on Thu Jul 06 17:15:51 CST 2017.
 */
public class PostFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(PerformancePostFilter.class);

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String filterType() {

        return "post";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }
    public static void main (String[] a){

        try {
            System.out.println(JWTUtils.createJWT("69","{\""+"role\":"+"3"+",\"deviceId\":\""+"000000006094d46b3b066c04147987e1"+"\"}", -1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        try {
            HttpServletRequest request = ctx.getRequest();
            String uri = request.getRequestURI();
            if ("/account-service/user/login".equals(uri) || "/account-service/user/mobile/login".equals(uri)) {
                if (ctx.getResponseStatusCode() == 200) {
                    try (final InputStream responseDataStream = ctx.getResponseDataStream()) {
                        Map<String,Object> response = JSON.parseObject(CharStreams.toString(new InputStreamReader(responseDataStream, "UTF-8")),Map.class);
                        if ((Integer) response.get("status") == 200) {
                            Map<String, Object> account = (Map<String, Object>) response.get("data");
                            String deviceId = request.getParameter("deviceId");
                            String role = account.get("usertype").toString();
                            Integer id = (Integer) account.get("id");
                            String idStr = String.valueOf(id);
                            if (id == null) idStr = "wrong";
                            String jwt = JWTUtils.createJWT(idStr,"{\""+"role\":"+role+",\"deviceId\":\""+deviceId+"\"}", -1);
                            //  ctx.addZuulResponseHeader("Access-Token",jwt);
                            response.put("accessToken", jwt);
                        }
                        ctx.setResponseBody(JSON.toJSONString(response));
                    } catch (IOException e) {
                        JSON jb = JSON.parseObject("{\"status\":210,\"message\":\"签名失败\"}");
                        ctx.setResponseBody(jb.toString());
                        e.printStackTrace();
                    }
                    return null;
                }
                return null;
            }else  if ("/account-service/admin/login".equals(uri)) {
                if (ctx.getResponseStatusCode() == 200) {
                    try (final InputStream responseDataStream = ctx.getResponseDataStream()) {
                        Map<String,Object> response = JSON.parseObject(CharStreams.toString(new InputStreamReader(responseDataStream, "UTF-8")),Map.class);
                        if ((Integer) response.get("status") == 200) {
                            Map<String, Object> admin = (Map<String, Object>) response.get("data");
                            //暂时无角色划分
                            Integer role = (Integer)admin.get("roleId");
                            Integer id = (Integer) admin.get("id");
                            String idStr = String.valueOf(id);
                            if (id == null) idStr = "wrong";
                            String jwt = JWTUtils.createJWT(idStr,"{\""+"role\":"+role+",\"deviceId\":\"websource\"}", -1);
                            response.put("accessToken", jwt);
                        }
                        ctx.setResponseBody(JSON.toJSONString(response));
                    } catch (IOException e) {
                        JSON jb = JSON.parseObject("{\"status\":210,\"message\":\"签名失败!\"}");
                        ctx.setResponseBody(jb.toString());
                        e.printStackTrace();
                    }
                    return null;
                }
                return null;
            }
            //图片处理
            try {
                 InputStream responseDataStream = ctx.getResponseDataStream();//会导致ctx.getResponseBody()值为空。客户端接收不到返回值。
                // System.out.println(responseDataStream);
                Map<String,Object> responsepic = JSON.parseObject(CharStreams.toString(new InputStreamReader(responseDataStream, "UTF-8")),Map.class);
                String data = responsepic.get("data").toString();
                System.out.println("data:"+data);

                Boolean is = false;
                for (String str : QiniuConstant.pictureMap.values()) {
                    if(data.contains(str)){
                        is = true ;
                        break;
                    }
                }
                if(is){
                    //对图片进行处理
                    //整个结果进行匹配，遍历JSONArray数组，将结果转换为JSONObject，根据文件名获取key，更改value。
                    JSONArray array = JSONArray.parseArray(data);
                    //array数组包含多个jsonObject对象,遍历多个对象
                    for(int i=0;i<array.size()-1;i++){
                        JSONObject object = JSON.parseObject(array.get(i).toString());
                        for (String str : QiniuConstant.pictureMap.values()) {//遍历map值
                            for (String s : object.keySet()) {//遍历返回结果值
                                Object value = object.get(s);
                                if (value.toString().contains(str)){//获取对应的key与value
                                    //logger.info("key1:"+s+"value1:"+object.get(s));
                                    //获取到了对应的图片路径value
                                    //根据deviceid做不同处理
                                    String deviceId = request.getHeader("deviceId");
                                    if("websource".equals(deviceId) || "0000000062728c586110c7f90033c587".equals(deviceId)){
                                        //图片后台做处理
                                        //统一返回 "http://" + domain + "/" + key 格式
                                        object.put(s, QiniuFile.getPrivateDownloadUrl(value.toString()));
                                    }else{
                                        //app自行处理
                                    }
                                    //logger.info("key2:"+s+"value2:"+object.get(s));
                                }
                            }
                        }
                        array.set(i,object);
                    }
                    //System.out.println("arry:"+array.toString());
                    responsepic.put("data",array);
                    ctx.setResponseBody(JSON.toJSONString(responsepic));
                    System.out.println("data1:"+array.toString());
                }else{
                    //System.out.println(ctx.getResponseDataStream());
                    ctx.setResponseBody(JSON.toJSONString(responsepic));//所以重新赋值
                }
            } catch (IOException e) {
                JSON jb = JSON.parseObject("{\"status\":210,\"message\":\"签名失败!\"}");
                ctx.setResponseBody(jb.toString());
                e.printStackTrace();
            }
            return null;
        } catch (Exception e) {
            JSON jb = JSON.parseObject("{\"status\":210,\"message\":\"签名失败\"}");
            ctx.setResponseBody(jb.toString());
            e.printStackTrace();
            return null;
        }
    }
}