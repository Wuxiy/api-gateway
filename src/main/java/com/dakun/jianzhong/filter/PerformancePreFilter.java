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
public class PerformancePreFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(PerformancePreFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return logger.isTraceEnabled();
    }

    @Override
    public Object run() {

        RequestContext context = RequestContext.getCurrentContext();
        String requestURI = context.getRequest().getRequestURI();

        StopWatch stopWatch = new StopWatch(requestURI);
        stopWatch.start(requestURI);
        context.set("performanceStopWatch", stopWatch);

        return null;
    }
}
