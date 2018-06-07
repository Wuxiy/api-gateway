package com.dakun.jianzhong.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dakun.jianzhong.controller.MicroserviceApiController;
import com.dakun.jianzhong.utils.JWTUtils;
import com.dakun.jianzhong.utils.ServerUtils;
import com.dakun.jianzhong.utils.TextUtils;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangh09 on Thu Jul 13 14:40:25 GMT+08:00 2017.
 */
public class PreFilter extends AbstractPathMatchingFilter {
    @Autowired
    private RestTemplate restTemplate;

    private RedisTemplate redisTemplate;

    @Resource
    private MicroserviceApiController microserviceApiController;

    @Autowired(required = false)
    public void setRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = stringRedisTemplate;
    }

    public PreFilter() {
        setFilterName("loginFilter");
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        try {
            //         RestTemplate restTemplate = new RestTemplate();
            HttpServletRequest request = ctx.getRequest();

            String uri = request.getRequestURI();

            //*************************************登陆验证码
            if ("/account-service/user/login".equals(uri) || "/account-service/user/mobile/login".equals(uri)) {
                Map<String, List<String>> requestParams = ctx.getRequestQueryParams();
                String code = requestParams.get("code").get(0);
                String phone = requestParams.get("mobile").get(0);
                ValueOperations<String, Object> operations = redisTemplate.opsForValue();
                String redisKey = TextUtils.getSMSRedisKey(phone);
                String storedCode = (String) operations.get(TextUtils.getSMSRedisKey(phone));
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
            } else if ("/account-service/admin/login".equals(uri)) {
                return null;
            }
            //*************************************处理权限
            /*HttpHeaders headers = new HttpHeaders();
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
*/
            String apiMap = microserviceApiController.getApiMap(uri);
            //API 不存在
            if (apiMap == null) {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(404);
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("message", "未找到该资源api");
                result.put("status", 404);
                ctx.setResponseBody(JSON.toJSONString(result));
                ctx.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
            } else {
                //API 存在，而且不公开
                if ("true".equals(apiMap)) {
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
                                ctx.addZuulRequestHeader("ADMIN-ID", claim.getId());
                                JSONObject object = JSONObject.parseObject(claim.getSubject());
                                ctx.addZuulRequestHeader("role", object.getString("role"));
                            } else {
                                Claims claim = JWTUtils.parseJWT(jwt);
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
