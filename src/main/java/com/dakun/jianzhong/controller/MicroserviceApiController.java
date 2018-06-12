package com.dakun.jianzhong.controller;

import com.dakun.jianzhong.utils.Result;
import com.dakun.jianzhong.utils.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by LiuQiulan
 *
 * @date 2018-6-6 18:55
 */
@RestController
@RequestMapping("/micro")
public class MicroserviceApiController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private StringRedisTemplate redisTemplate;

    ValueOperations<String,String> valueOperations;
    //构造方法注入
    MicroserviceApiController(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
        valueOperations = redisTemplate.opsForValue();
    }
    Map<String,Object> apiMap = new HashMap<>();

    @PostMapping("/addApies")
    public Result addApies(@RequestParam Map<String,Object> params){
        //this.apiMap.putAll(params);
        Set<Map.Entry<String, Object>> entries = params.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            valueOperations.set(entry.getKey(),entry.getValue().toString(),1, TimeUnit.HOURS);
        }
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/getApies")
    public String getApiMap(String key){
        return valueOperations.get(key);
    }
}
