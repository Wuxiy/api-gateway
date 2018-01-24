package com.dakun.jianzhong.filter;

import com.alibaba.fastjson.JSON;
import com.dakun.jianzhong.service.qiniu.QiniuConstant;
import com.dakun.jianzhong.service.qiniu.QiniuFile;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author wangjie
 * @date 1/17/2018
 */
public class QiniuImageFilter extends AbstractPathMatchingFilter {

    public QiniuImageFilter() {
        setFilterName("imageFilter");
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String uri = request.getRequestURI();

        try {
            String fileName = uri.split("/image/")[1];
            if (fileName == null) {
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("message", "没有图片名称");
                result.put("status", 404);
                ctx.setResponseBody(JSON.toJSONString(result));
                ctx.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
            }
            Enumeration<String> enumerator = request.getParameterNames();
            StringBuilder builder = new StringBuilder("");
            while (enumerator.hasMoreElements()) {
                builder.append(enumerator.nextElement());
            }
            String imageType = builder.toString();
            String downloadURL = QiniuFile.getdownloadurl(QiniuConstant.Domain_resources, fileName, imageType, QiniuConstant.portrait_download_app_exp);
            Map<String, List<String>> qp = new HashMap<String, List<String>>();
            String paramsPairs = downloadURL.split("\\?")[1];
            String[] params = paramsPairs.split("&");

            for (int i = 0; i < params.length; i++) {
                List<String> paramList = new ArrayList<>();
                paramList.add(params[i + 1]);
                qp.put(params[i], paramList);
                i++;
            }
            ctx.setRequestQueryParams(qp);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("message", "图片地址错误");
            result.put("status", 404);
            ctx.setResponseBody(JSON.toJSONString(result));
            ctx.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
        }

        return null;
    }

}
