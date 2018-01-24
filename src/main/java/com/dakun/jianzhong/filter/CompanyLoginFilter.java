package com.dakun.jianzhong.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangjie
 * @date 1/17/2018
 */
public class CompanyLoginFilter extends AbstractPathMatchingFilter {

    private static Logger logger = LoggerFactory.getLogger(CompanyLoginFilter.class);

    public CompanyLoginFilter() {
        setFilterName("companyLoginFilter");
    }

    @Override
    public Object run() {
        logger.debug("Enter CompanyLoginFilter");
        return null;
    }

}
