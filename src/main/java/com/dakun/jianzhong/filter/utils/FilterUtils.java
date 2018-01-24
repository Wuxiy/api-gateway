package com.dakun.jianzhong.filter.utils;

import com.dakun.jianzhong.utils.MD5;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Map;
import java.util.Set;

/**
 * @author wangjie
 * @date 1/17/2018
 */
public class FilterUtils {

    private static Logger logger = LoggerFactory.getLogger(FilterUtils.class);

    private static PathMatcher pathMatcher = new AntPathMatcher();

    private FilterUtils() {}

    public static Set<String> antMatcher(Map<String, Set<String>> filterDefinition, String url) {

        logger.debug("antMatcher -> requestUri: {}", url);
        for (Map.Entry<String, Set<String>> entry : filterDefinition.entrySet()) {
            String chainName = entry.getKey();
            Set<String> filterNames = entry.getValue();
            if (pathMatcher.match(chainName, url)) {
                return filterNames;
            }
        }

        return Sets.newHashSet();
    }

    public static void main(String[] args) {
        System.out.println(MD5.getMD5String("135246"));
    }
}
