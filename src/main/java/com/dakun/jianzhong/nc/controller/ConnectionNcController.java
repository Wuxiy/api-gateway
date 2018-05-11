package com.dakun.jianzhong.nc.controller;

import com.dakun.jianzhong.nc.model.QcReportbillNc;
import com.dakun.jianzhong.nc.model.QcReportbillNcVo;
import com.dakun.jianzhong.nc.service.QcReportbillNcClientService;
import com.dakun.jianzhong.utils.Result;
import com.dakun.jianzhong.utils.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/NC")
public class ConnectionNcController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private QcReportbillNcClientService qcReportbillNcClientService;

    @GetMapping("/getNCdata")
    public Result getNC(@RequestParam Map<String,Object> params){
        QcReportbillNcVo qcReportbillNcVo = new QcReportbillNcVo();
        qcReportbillNcVo.setCreateTime((String) params.get("createTime"));
        qcReportbillNcVo.setCode((String) params.get("code"));
        qcReportbillNcVo.setWorkshopPK((String) params.get("workshopPK"));
        qcReportbillNcVo.setTimes((String) params.get("times"));
        return ResultGenerator.genSuccessResult(qcReportbillNcClientService.getNC(qcReportbillNcVo));
    }
}
