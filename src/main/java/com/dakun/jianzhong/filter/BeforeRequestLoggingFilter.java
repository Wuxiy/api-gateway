package com.dakun.jianzhong.filter;

import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>User: wangjie
 * <p>Date: 11/29/2017
 */
public class BeforeRequestLoggingFilter extends AbstractRequestLoggingFilter {

    private static final Logger logger = LoggerFactory.getLogger(BeforeRequestLoggingFilter.class);

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        logger.debug(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.debug(message);
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        return logger.isDebugEnabled();
    }

    @Override
    public Object run() {

        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest requestToUse = context.getRequest();

		boolean shouldLog = shouldLog(requestToUse);
		if (shouldLog) {
			beforeRequest(requestToUse, getBeforeMessage(requestToUse));
		}

        return null;
    }
}
