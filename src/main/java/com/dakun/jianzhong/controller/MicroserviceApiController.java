package com.dakun.jianzhong.controller;

import com.dakun.jianzhong.utils.Result;
import com.dakun.jianzhong.utils.ResultGenerator;
import com.sun.org.apache.regexp.internal.RE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LiuQiulan
 *
 * @date 2018-6-6 18:55
 */
@RestController
@RequestMapping("/micro")
public class MicroserviceApiController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    Map<String,Object> apiMap = new HashMap<>();

    @PostMapping("/addApies")
    public Result addApies(@RequestParam Map<String,Object> params){
        this.apiMap.putAll(params);
        return ResultGenerator.genSuccessResult(apiMap);
    }

    @GetMapping("/getApies")
    public Map<String,Object> getApiMap(){
        return this.apiMap;
    }
}
