package com.dakun.jianzhong.filter;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author wangjie
 * @date 1/17/2018
 */
@ConfigurationProperties(prefix = "zhuannong.filter")
public class FilterMatchProperties {

    private Logger logger = LoggerFactory.getLogger(FilterMatchProperties.class);

    private Map<String, String> filterChainDefinitions = new LinkedHashMap<>();

    private Map<String, Set<String>> filterChainSet = new LinkedHashMap<>();

    public Map<String, String> getFilterChainDefinitions() {
        return filterChainDefinitions;
    }

    public void setFilterChainDefinitions(Map<String, String> filterChainDefinitions) {
        this.filterChainDefinitions = filterChainDefinitions;
    }

    public Map<String, Set<String>> getFilterChainSet() {
        return filterChainSet;
    }

    @PostConstruct
    public void checkFilters() {
        if (logger.isInfoEnabled()) {
            logger.info("Original filter chain definition: {}", JSON.toJSONString(filterChainDefinitions));
        }

        filterChainDefinitions.forEach((chainName, filterNames) -> {
            if (!StringUtils.isEmpty(filterNames)) {
                filterChainSet.put(chainName, StringUtils.commaDelimitedListToSet(filterNames));
            }
        });

        if (logger.isInfoEnabled()) {
            logger.info("Parse filter chain definition: {}", JSON.toJSONString(filterChainSet));
        }
    }
}
