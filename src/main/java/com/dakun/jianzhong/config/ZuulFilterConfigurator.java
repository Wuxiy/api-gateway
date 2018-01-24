package com.dakun.jianzhong.config;

import com.dakun.jianzhong.filter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangjie
 * @date 1/17/2018
 */
@Configuration
public class ZuulFilterConfigurator {

    @Bean
    public FilterMatchProperties filterMatchProperties() {
        return new FilterMatchProperties();
    }

    @Bean
    public PreFilter accessFilter(FilterMatchProperties filterMatchProperties) {
        PreFilter preFilter = new PreFilter();
        preFilter.setFilterMatchProperties(filterMatchProperties);
        return preFilter;
    }

    @Bean
    public PostFilter postFilter() {
        return new PostFilter();
    }

    @Bean
    public BeforeRequestLoggingFilter beforeRequestLoggingFilter() {

        BeforeRequestLoggingFilter filter = new BeforeRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);

        return filter;
    }

    @Bean
    public PerformancePreFilter performancePreFilter() {
        return new PerformancePreFilter();
    }

    @Bean
    public PerformancePostFilter performancePostFilter() {
        return new PerformancePostFilter();
    }

    @Bean
    public QiniuImageFilter qiniuImageFilter(FilterMatchProperties filterMatchProperties) {
        QiniuImageFilter qiniuImageFilter = new QiniuImageFilter();
        qiniuImageFilter.setFilterMatchProperties(filterMatchProperties);
        return qiniuImageFilter;
    }

    @Bean
    public CompanyLoginFilter companyLoginFilter(FilterMatchProperties filterMatchProperties) {
        CompanyLoginFilter companyLoginFilter = new CompanyLoginFilter();
        companyLoginFilter.setFilterMatchProperties(filterMatchProperties);
        return companyLoginFilter;
    }
}
