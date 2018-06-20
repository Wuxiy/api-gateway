package com.dakun.jianzhong.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dakun.jianzhong.service.qiniu.QiniuConstant;
import com.dakun.jianzhong.service.qiniu.QiniuFile;
import com.dakun.jianzhong.utils.BeanUtils;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static void main(String[] a) {

        try {
            System.out.println(JWTUtils.createJWT("69", "{\"" + "role\":" + "3" + ",\"deviceId\":\"" + "000000006094d46b3b066c04147987e1" + "\"}", -1));
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
                        Map<String, Object> response = JSON.parseObject(CharStreams.toString(new InputStreamReader(responseDataStream, "UTF-8")), Map.class);
                        if ((Integer) response.get("status") == 200) {
                            Map<String, Object> account = (Map<String, Object>) response.get("data");
                            String deviceId = request.getParameter("deviceId");
                            String role = account.get("usertype").toString();
                            Integer id = (Integer) account.get("id");
                            String idStr = String.valueOf(id);
                            if (id == null) idStr = "wrong";
                            String jwt = JWTUtils.createJWT(idStr, "{\"" + "role\":" + role + ",\"deviceId\":\"" + deviceId + "\"}", -1);
                            //  ctx.addZuulResponseHeader("Access-Token",jwt);
                            response.put("accessToken", jwt);
                        }
                        //防止数据本身过大导致缓存不够用
                        InputStream rs = new ByteArrayInputStream(JSON.toJSONString(response).getBytes());
                        ctx.setResponseDataStream(rs);
                        //ctx.setResponseBody(JSON.toJSONString(response));
                    } catch (IOException e) {
                        JSON jb = JSON.parseObject("{\"status\":210,\"message\":\"签名失败\"}");
                        ctx.setResponseBody(jb.toString());
                        e.printStackTrace();
                    }
                    return null;
                }
                return null;
            } else if ("/account-service/admin/login".equals(uri)) {
                if (ctx.getResponseStatusCode() == 200) {
                    try (final InputStream responseDataStream = ctx.getResponseDataStream()) {
                        Map<String, Object> response = JSON.parseObject(CharStreams.toString(new InputStreamReader(responseDataStream, "UTF-8")), Map.class);
                        if ((Integer) response.get("status") == 200) {
                            Map<String, Object> admin = (Map<String, Object>) response.get("data");
                            //暂时无角色划分
                            Integer role = (Integer) admin.get("roleId");
                            Integer id = (Integer) admin.get("id");
                            String idStr = String.valueOf(id);
                            if (id == null) idStr = "wrong";
                            String jwt = JWTUtils.createJWT(idStr, "{\"" + "role\":" + role + ",\"deviceId\":\"websource\"}", -1);
                            response.put("accessToken", jwt);
                        }
                        InputStream rs = new ByteArrayInputStream(JSON.toJSONString(response).getBytes());
                        ctx.setResponseDataStream(rs);
                        //ctx.setResponseBody(JSON.toJSONString(response));
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
                InputStream responseDataStream = ctx.getResponseDataStream();//会导致输入流不可复用。客户端接收不到返回值。
                // System.out.println(responseDataStream);
                String s = CharStreams.toString(new InputStreamReader(responseDataStream, "UTF-8"));
                //针对返回结果不是map的数据
                if(!s.contains("{")){
                    InputStream rs = new ByteArrayInputStream(s.getBytes());
                    ctx.setResponseDataStream(rs);
                    return null;
                }
                Map<String, Object> responsepic = JSON.parseObject(s, Map.class);
                String data = responsepic.get("data").toString();
                System.out.println("data:" + data);

                String deviceId = request.getHeader("deviceId");
                if ("websource".equals(deviceId) || "0000000062728c586110c7f90033c587".equals(deviceId)) {
                    Boolean is = false;
                    for (String str : QiniuConstant.pictureMap.values()) {
                        if (data.contains(str)) {
                            is = true;
                            break;
                        }
                    }
                    if (is) {
                        //对图片进行处理：结果可能是数组也可能是对象
                        //整个结果进行匹配，遍历JSONObject对象，根据文件名获取key，更改value。
                        //整个结果进行匹配，遍历JSONArray数组，将结果转换为JSONObject，根据文件名获取key，更改value。
                        if (data.startsWith("[")) {
                            JSONArray array = JSONArray.parseArray(data);
                            //array数组包含多个jsonObject对象,遍历多个对象
                            for (int i = 0; i < array.size() - 1; i++) {
                                JSONObject object = setImageUrl(array.get(i).toString());
                                array.set(i, object);
                            }
                            //System.out.println("arry:"+array.toString());
                            responsepic.put("data", array);
                            //ctx.setResponseBody(JSON.toJSONString(responsepic));
                            InputStream rs = new ByteArrayInputStream(JSON.toJSONString(responsepic).getBytes());
                            ctx.setResponseDataStream(rs);
                            System.out.println("data1:" + array.toString());
                        } else {
                            JSONObject object = setImageUrl(data);
                            responsepic.put("data", object);
                            //ctx.setResponseBody(JSON.toJSONString(responsepic));
                            InputStream rs = new ByteArrayInputStream(JSON.toJSONString(responsepic).getBytes());
                            ctx.setResponseDataStream(rs);
                            System.out.println("data1:" + object.toString());
                        }
                    } else {
                        // 所以重新创建流并通过ctx对象传递流
                        InputStream rs = new ByteArrayInputStream(JSON.toJSONString(responsepic).getBytes());
                        ctx.setResponseDataStream(rs);
                    }
                }else {
                    //app自行处理
                    // 所以重新创建流并通过ctx对象传递流
                    InputStream rs = new ByteArrayInputStream(JSON.toJSONString(responsepic).getBytes());
                    ctx.setResponseDataStream(rs);
                }
            } catch (Exception e) {
                JSON jb = JSON.parseObject("{\"status\":210,\"message\":\"业务方法传递数据失败!\"}");
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

    //对value遍历处理
    public JSONObject setImageUrl(String data) {
        JSONObject object = JSONObject.parseObject(data);
        for (String str : QiniuConstant.pictureMap.values()) {//遍历map值
            for (String s : object.keySet()) {//遍历返回结果值
                Object value = object.get(s);
                if (value.toString().contains(str)) {//获取对应的key与value
                    if (value.toString().contains("{")) {
                        //value是其他对象
                        object.put(s, buildObj(value, str));
                    } else {
                        //获取到了对应的图片路径value
                        //value可能是一个图片数组
                        //图片后台做处理
                        //统一返回 "http://" + domain + "/" + key 格式
                        object.put(s, ImageToUrl(value.toString()));
                    }
                }
            }
        }
        return object;
    }

    //包装图片地址
    public String ImageToUrl(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] imageFileNames = str.split(";");
        for (String image : imageFileNames) {
            stringBuilder.append(QiniuFile.getPrivateDownloadUrl(image)).append(";");
        }
        return stringBuilder.toString();
    }

    //将object对象转换为map包装图片地址
    public Map<String, Object> ObjectToMap(Map<String, Object> map, String str) {
        //Map<String, Object> map = BeanUtils.beanProperties(obj);
        //JSONObject map = JSONObject.parseObject(obj.toString());
        Map<String, Object> resultMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if(entry.getValue().toString().contains("[")){
                buildObj(entry.getValue(),str);
            }
            if (entry.getValue().toString().contains(str)) {
                resultMap.put(entry.getKey(), ImageToUrl(entry.getValue().toString()));
                continue;
            }
            resultMap.put(entry.getKey(), entry.getValue());
        }
        return resultMap;
    }

    //对包含图片的数据进行包装
    public Object buildObj(Object object, String str) {
        //Map<String, Object> map = BeanUtils.beanProperties(object);不能用BeanUtils方法
        String s = object.toString();
        if (s.startsWith("[")) {
            JSONArray arry = JSONArray.parseArray(s);
            for (int i = 0; i <arry.size(); i++) {
                if(arry.get(i).toString().contains(str)){
                    JSONObject arrayObj = JSON.parseObject(arry.get(i).toString());
                    arry.set(i, ObjectToMap(arrayObj, str));
                }
            }
            return arry;
        } else {
            //对象
            JSONObject object1 = JSON.parseObject(s);
            return object1;
        }
    }
}