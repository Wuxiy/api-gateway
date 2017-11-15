package com.dakun.jianzhong.filter;

import com.alibaba.fastjson.JSON;
import com.dakun.jianzhong.utils.JWTUtils;
import com.google.common.io.CharStreams;
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
            System.out.println(JWTUtils.createJWT("35","{\""+"role\":"+"1"+",\"deviceId\":\""+"fffffffffba49af767a0185117f97614"+"\"}", -1));
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
            if (uri.equals("/account-service/user/login")) {
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
            }else  if (uri.equals("/account-service/admin/login")) {
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
            return null;
        } catch (Exception e) {
            JSON jb = JSON.parseObject("{\"status\":210,\"message\":\"签名失败\"}");
            ctx.setResponseBody(jb.toString());
            e.printStackTrace();
            return null;
        }
    }
}