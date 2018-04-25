package com.dakun.jianzhong.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dakun.jianzhong.utils.ServerUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by wangh09 on 2017/9/11.
 */
@Service
public class BaiduLBSService {
    private RestTemplate restTemplate = new RestTemplate();
    public String createQALBSLocation(double longitude, double latitude, String title) {
        String baiduId = getQALBSLocationId(longitude,latitude,title);
        return baiduId;
    }
    public String getQALBSLocationId(double longitude, double latitude, String title) {
        String QALBSId = null;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        String bounds = String.format("%f,%f;%f,%f", longitude-0.0001,latitude-0.0001,longitude+0.0001,latitude+0.0001);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ServerUtils.BAIDU_LBS_POI_LIST)
                .queryParam("title", title)
                .queryParam("bounds",bounds)
                .queryParam("geotable_id",ServerUtils.BAIDU_GEOTABLE)
                .queryParam("ak",ServerUtils.BAIDU_AK)
                .queryParam("tags","问答")
                .queryParam("page_size",1);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<String> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                entity,
                String.class);
        System.out.println(response.toString());
        JSONObject responseStr = JSONObject.parseObject(response.getBody());
        if(responseStr.getInteger("status") == 0 && responseStr.getInteger("size") > 0) {
            JSONArray poiArray = responseStr.getJSONArray("pois");
            Map<String,Object> pois = (Map<String,Object>)poiArray.get(0);
            QALBSId = pois.get("id").toString();
            System.out.println("location exists.");
        } else {
            MultiValueMap<String, String> param = new LinkedMultiValueMap<String, String>();
            param.add("title", title);
            param.add("geotable_id",ServerUtils.BAIDU_GEOTABLE);
            param.add("ak",ServerUtils.BAIDU_AK);
            param.add("longitude",String.format("%f",longitude));
            param.add("latitude",String.format("%f",latitude));
            param.add("coord_type",ServerUtils.COORD_TYPE);
            param.add("tags","问答");
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<MultiValueMap<String, String>>(param,headers);
            HttpEntity<String> res=restTemplate.exchange(ServerUtils.BAIDU_LBS_POI_CREATE,HttpMethod.POST,req,String.class);
            System.out.println(res.toString());
            JSONObject createStr = JSONObject.parseObject(res.getBody());
            if(createStr.getInteger("status") == 0) {
                QALBSId = createStr.getString("id");
            }
        }
        return QALBSId;
    }

    public JSONObject geocoder(String address){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        MultiValueMap<String, String> param = new LinkedMultiValueMap<String, String>();
        param.add("address", address);
        param.add("output","json");
        param.add("ak",ServerUtils.BAIDU_AK);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<MultiValueMap<String, String>>(param,headers);
        HttpEntity<String> res=restTemplate.exchange(ServerUtils.BAIDU_GEOCODING_URL,HttpMethod.POST,req,String.class);
        System.out.println(res.toString());
        JSONObject json =  JSONObject.parseObject(res.getBody());
        if(json.getInteger("status") == 0) {
            return json.getJSONObject("result").getJSONObject("location");
        }
        return null;
    }

    public JSONObject getTimeZone(Double longitude,Double latitude){
        //当前绝对时间（秒）
        long curSec = System.currentTimeMillis()/1000;
        String URL= ServerUtils.BAIDU_LBS_TIMEZONE+"?coord_type=ctype&location=lat,lng&timestamp=ts&ak=key";
        URL = URL.replace("ctype","bd09ll")
           .replace("lat",String.valueOf(latitude))
           .replace("lng",String.valueOf(longitude))
           .replace("ts",String.valueOf(curSec))
           .replace("key",ServerUtils.BAIDU_AK);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<MultiValueMap<String, String>>(null,headers);
        HttpEntity<String> res=restTemplate.exchange(URL,HttpMethod.GET,req,String.class);
        if(res.getBody() != null){
            try{
                return JSONObject.parseObject(res.getBody());
            }catch(Exception e){
                return null;
            }
        }
        return null;
    }

}
