package com.dakun.jianzhong.nc.service;
import com.dakun.jianzhong.nc.config.ApiServiceProperties;
import com.dakun.jianzhong.nc.model.QcReportbillNc;
import com.dakun.jianzhong.nc.model.QcReportbillNcVo;
import com.dakun.jianzhong.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

/**
 * <p>User: liuqiulan
 * <p>Date: 5/9/2018
 * @author liuqiulan
 */
@Service
public class QcReportbillNcClientService {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApiServiceProperties apiServiceProperties;

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public HashMap getNC(QcReportbillNcVo qcReportbillNcVo) {

            if (qcReportbillNcVo == null) {
                return null;
            }

            logger.debug("qcReportbillNcVo [{}]", qcReportbillNcVo.toString());

            // exchange支持‘含参数的类型’（即泛型类）作为返回类型，该特性通过‘ParameterizedTypeReference<T>responseType’描述。
            String apiUrl = apiServiceProperties.getUrl();

           /* ResponseEntity<List<QcReportbillNc>> exchange = restTemplate()
                    .exchange(apiUrl+"/reportbill/nc/getNC?createTime="
                                    +qcReportbillNcVo.getCreateTime()+"&code="
                                    +qcReportbillNcVo.getCode()+"&workshopPK="
                                    +qcReportbillNcVo.getWorkshopPK()+"&times="
                                    +qcReportbillNcVo.getTimes(),
                            HttpMethod.GET, null, new ParameterizedTypeReference<List<QcReportbillNc>>(){});*/
        ResponseEntity<HashMap> exchange = restTemplate()
                .exchange(apiUrl + "/reportbill/nc/getNC?createTime="
                                + qcReportbillNcVo.getCreateTime() + "&code="
                                + qcReportbillNcVo.getCode() + "&workshopPK="
                                + qcReportbillNcVo.getWorkshopPK() + "&times="
                                + qcReportbillNcVo.getTimes(),
                        HttpMethod.GET, null, HashMap.class);

        if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }

            return null;
        }
    }


