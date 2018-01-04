package com.dakun.jianzhong.controller;

import com.aliyuncs.exceptions.ClientException;
import com.dakun.jianzhong.service.sms.CommonSMS;
import com.dakun.jianzhong.service.sms.SMSConstant;
import com.dakun.jianzhong.utils.Result;
import com.dakun.jianzhong.utils.ResultGenerator;
import com.dakun.jianzhong.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * Created by wangh09 on 2017/7/27.
 */
@RestController
@RequestMapping("/sms")
public class SmsController {
    private static final Integer EXPIRE_SECONDS = 600;
    private static final Integer FREEZE_SECONDS = 60;
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
    @Autowired
    public SmsController(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/get-login")
    public Result login(@RequestParam(required = true) String phone,@RequestParam(required = true) String hash) {
        if(!TextUtils.getHashedPhone(phone).equals(hash.toLowerCase())) {
            return ResultGenerator.genFailResult("wrong");
        }
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        String redisKey = TextUtils.getSMSRedisKey(phone);
        Long seconds = redisTemplate.getExpire(redisKey,TimeUnit.SECONDS);
        if(seconds >= EXPIRE_SECONDS - FREEZE_SECONDS) {
            return ResultGenerator.genFailResult("频繁获取验证码");
        }
        String code = TextUtils.getRandNum(4);
       //正式上线之前
        try {
            CommonSMS.sendcommonmsg(
                    phone,
                    SMSConstant.SIGN_OF_DAKUNKEJI,
                    SMSConstant.LOGIN_CODE,"{\"code\":"+code+"}");
            operations.set(redisKey, code);
            redisTemplate.expire(redisKey, EXPIRE_SECONDS, TimeUnit.SECONDS);
            return ResultGenerator.genSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult(e.getMessage());
        }
    }

    @GetMapping("/check")
    public Result check(@RequestParam(required = true) String phone,@RequestParam(required = true) String code) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        String redisKey = TextUtils.getSMSRedisKey(phone);
        String storedCode = (String)operations.get(TextUtils.getSMSRedisKey(phone));
        if(storedCode == null) {
            return ResultGenerator.genFailResult("expired");
        }
        redisTemplate.delete(redisKey);
        if(code.equals(storedCode)) {
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("wrong");
    }
}
