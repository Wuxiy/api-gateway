package com.dakun.jianzhong.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dakun.jianzhong.service.qiniu.QiniuConstant;
import com.dakun.jianzhong.service.qiniu.QiniuFile;
import com.dakun.jianzhong.utils.JWTUtils;
import com.dakun.jianzhong.utils.ServerUtils;
import com.dakun.jianzhong.utils.TextUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by wangh09 on Thu Jul 13 14:40:25 GMT+08:00 2017.
 */
public class PreFilter extends ZuulFilter {
    @Autowired
    private RestTemplate restTemplate;

    private RedisTemplate redisTemplate;

    @Autowired(required = false)
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
    }

    public PreFilter() {
    }

    @Override
    public String filterType() {

        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        try {
            //         RestTemplate restTemplate = new RestTemplate();
            HttpServletRequest request = ctx.getRequest();

            String uri = request.getRequestURI();

            //*************************************处理图片
            if (uri.startsWith("/image/")) {
                try {
                    String fileName = uri.split("/image/")[1];
                    if (fileName == null) {
                        Map<String, Object> result = new HashMap<String, Object>();
                        result.put("message", "没有图片名称");
                        result.put("status", 404);
                        ctx.setResponseBody(JSON.toJSONString(result));
                        ctx.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
                    }
                    Enumeration<String> enumerator = request.getParameterNames();
                    StringBuilder builder = new StringBuilder("");
                    while (enumerator.hasMoreElements()) {
                        builder.append(enumerator.nextElement());
                    }
                    String imageType = builder.toString();
                    String downloadURL = QiniuFile.getdownloadurl(QiniuConstant.Domain_resources, fileName, imageType, QiniuConstant.portrait_download_app_exp);
                    Map<String, List<String>> qp = new HashMap<String, List<String>>();
                    String paramsPairs = downloadURL.split("\\?")[1];
                    String[] params = paramsPairs.split("&");

                    for (int i = 0; i < params.length; i++) {
                        List<String> paramList = new ArrayList<>();
                        paramList.add(params[i + 1]);
                        qp.put(params[i], paramList);
                        i++;
                    }
                    ctx.setRequestQueryParams(qp);
                } catch (Exception e) {
                    Map<String, Object> result = new HashMap<String, Object>();
                    result.put("message", "图片地址错误");
                    result.put("status", 404);
                    ctx.setResponseBody(JSON.toJSONString(result));
                    ctx.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
                }

                return null;
            }

            //*************************************登陆验证码
            if (uri.equals("/account-service/user/login")) {
                Map<String, List<String>> requestParams = ctx.getRequestQueryParams();
                String code = requestParams.get("code").get(0);
                String phone = requestParams.get("mobile").get(0);
                ValueOperations<String, Object> operations = redisTemplate.opsForValue();
                String redisKey = TextUtils.getSMSRedisKey(phone);
               /* String storedCode = (String) operations.get(TextUtils.getSMSRedisKey(phone));*/
                String storedCode = (String) operations.get(TextUtils.getSMSRedisKey(""));
                if (storedCode == null) {
                    ctx.setResponseStatusCode(401);
                    Map<String, Object> result = new HashMap<String, Object>();
                    result.put("message", "验证码已过期,请重新获取！");
                    result.put("status", 401);
                    ctx.setSendZuulResponse(false);
                    ctx.setResponseBody(JSON.toJSONString(result));
                    ctx.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
                    return null;
                }
                if (!code.equals(storedCode)) {
                    ctx.setResponseStatusCode(401);
                    Map<String, Object> result = new HashMap<String, Object>();
                    result.put("message", "验证码错误！");
                    result.put("status", 401);
                    ctx.setSendZuulResponse(false);
                    ctx.setResponseBody(JSON.toJSONString(result));
                    ctx.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
                    return null;
                }
                redisTemplate.delete(redisKey);
                return null;
            } else if (uri.equals("/account-service/admin/login")) {
                return null;
            }
            //*************************************处理权限
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ServerUtils.API_LIST_URL)
                    .queryParam("api", uri);

            HttpEntity<Map> response = restTemplate.exchange(
                    builder.build().encode().toUri(),
                    HttpMethod.GET,
                    entity,
                    Map.class);

            Map<String, Object> res = response.getBody();
            List<Map<String, Object>> data = (List<Map<String, Object>>) res.get("data");

            //API 不存在
            if (data.size() < 1) {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(404);
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("message", "未找到该资源api");
                result.put("status", 404);
                ctx.setResponseBody(JSON.toJSONString(result));
                ctx.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
            } else {
                //API 存在，而且不公开
                if (!(Boolean) data.get(0).get("isPublic")) {
                    String jwt = getToken(request);
                    //没有jwt
                    if (jwt == null) {
                        ctx.setSendZuulResponse(false);
                        ctx.setResponseStatusCode(401);
                        Map<String, Object> result = new HashMap<String, Object>();
                        result.put("message", "用户未登陆");
                        result.put("status", 401);
                        ctx.setResponseBody(JSON.toJSONString(result));
                        ctx.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
                    } else {
                        try {
                            String deviceIdParam = getDeviceId(request);
                            //如果是网站来源
                            if ("websource".equals(deviceIdParam)) {
                                Claims claim = JWTUtils.parseJWT(jwt);
                                ctx.addZuulRequestHeader("adminId", claim.getId());
                                JSONObject object = JSONObject.parseObject(claim.getSubject());
                                ctx.addZuulRequestHeader("role", object.getString("role"));
                            } else {
                                Claims claim = JWTUtils.parseJWT(jwt);
                                System.out.println("accountId:" + claim.getId());
                                System.out.println("subject:" + claim.getSubject());
                                ctx.addZuulRequestHeader("accountId", claim.getId());
                                JSONObject object = JSONObject.parseObject(claim.getSubject());
                                ctx.addZuulRequestHeader("usertype", object.getString("role"));
                                String deviceId = object.getString("deviceId");
                                if (!deviceId.equals(deviceIdParam)) {
                                    ctx.setSendZuulResponse(false);
                                    ctx.setResponseStatusCode(402);
                                    Map<String, Object> result = new HashMap<String, Object>();
                                    result.put("message", "登陆异常");
                                    result.put("status", 402);
                                    ctx.setResponseBody(JSON.toJSONString(result));
                                    ctx.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
                                }
                            }
                            //读取claim, 增加权限控制
                        } catch (Exception e) {
                            //有jwt,但是无法正常解析，
                            ctx.setSendZuulResponse(false);
                            ctx.setResponseStatusCode(401);
                            Map<String, Object> result = new HashMap<String, Object>();
                            result.put("message", "登陆异常或登陆状态已过期");
                            result.put("status", 401);
                            ctx.setResponseBody(JSON.toJSONString(result));
                            ctx.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(500);
            return null;
        }
    }

    private String getDeviceId(HttpServletRequest request) {
        String deviceId = request.getHeader("deviceId");
        if (StringUtils.isEmpty(deviceId)) {
            return request.getParameter("deviceId");
        }
        return deviceId;
    }

    private String getToken(HttpServletRequest request) {
        String jwt = request.getHeader("Access-Token");
        if (StringUtils.isEmpty(jwt)) {
            return request.getParameter("token");
        }
        return jwt;
    }
}
