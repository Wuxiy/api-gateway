package com.dakun.jianzhong.nc.controller;

import com.dakun.jianzhong.nc.model.QcReportbillNcVo;
import com.dakun.jianzhong.nc.service.QcReportbillNcClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/NC")
public class ConnectionNcController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private QcReportbillNcClientService qcReportbillNcClientService;

    @GetMapping("/getNCdata")
    public List getNC(QcReportbillNcVo qcReportbillNcVo){
        return qcReportbillNcClientService.getNC(qcReportbillNcVo);
    }
}
