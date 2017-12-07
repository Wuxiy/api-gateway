package com.dakun.jianzhong.controller;

import com.alibaba.fastjson.JSONObject;
import com.dakun.jianzhong.service.BaiduLBSService;
import com.dakun.jianzhong.utils.Result;
import com.dakun.jianzhong.utils.ResultGenerator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by hexingfu on 2017/9/13.
 */
@RestController
@RequestMapping("/map-service/baidu-map")
public class MapController {
    @Resource
    BaiduLBSService baiduLBSService;
    /**
     * 根据街道地址解析经纬度
     * @param address
     * @return
     */
    @RequestMapping("/geocoder")
    public Result geocoder(@RequestParam(required = true,name="address")String address){
        JSONObject json = baiduLBSService.geocoder(address);
        if(json == null){
            return ResultGenerator.genFailResult("地址解析失败，请填写精确的地址！");
        }
        return ResultGenerator.genSuccessResult(json);
    }

    /**
     * 创建lbs云地址
     */
    @RequestMapping("/createQALBSLocation")
    public Result createQALBSLocation( @RequestBody  Map<String, Object> params){
         Double longitude = Double.parseDouble(params.get("longitude").toString());
         Double latitude = Double.parseDouble(params.get("latitude").toString());
         String title = (String)params.get("title");
        return ResultGenerator.genSuccessResult(baiduLBSService.createQALBSLocation(longitude,latitude,title));
    }
}
