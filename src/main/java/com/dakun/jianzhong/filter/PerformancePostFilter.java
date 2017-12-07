package com.dakun.jianzhong.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

/**
 * <p>User: wangjie
 * <p>Date: 11/29/2017
 */
public class PerformancePostFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(PerformancePostFilter.class);

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return logger.isInfoEnabled();
    }

    @Override
    public Object run() {

        RequestContext context = RequestContext.getCurrentContext();

        StopWatch stopWatch = (StopWatch) context.get("performanceStopWatch");
        stopWatch.stop();

        if (logger.isInfoEnabled()) {
            logger.info(stopWatch.shortSummary());
        }

        return null;
    }
}
