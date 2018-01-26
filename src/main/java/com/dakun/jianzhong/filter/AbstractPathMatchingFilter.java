package com.dakun.jianzhong.filter;

import com.dakun.jianzhong.filter.utils.FilterUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * @author wangjie
 * @date 1/18/2018
 */
public abstract class AbstractPathMatchingFilter extends ZuulFilter {

    private String filterName;

    private FilterMatchProperties filterMatchProperties;

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public FilterMatchProperties getFilterMatchProperties() {
        return filterMatchProperties;
    }

    public void setFilterMatchProperties(FilterMatchProperties filterMatchProperties) {
        this.filterMatchProperties = filterMatchProperties;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {

        Map<String, Set<String>> filterChainSet = getFilterMatchProperties().getFilterChainSet();

        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String requestURI = request.getRequestURI();
        Set<String> filterNames = FilterUtils.antMatcher(filterChainSet, requestURI);

        return filterNames.contains(filterName);
    }
}
